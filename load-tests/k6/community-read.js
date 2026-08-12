import http from 'k6/http';
import { check, fail, sleep } from 'k6';

const BASE_URL = (__ENV.BASE_URL || 'http://140.245.70.133').replace(/\/$/, '');
const PROFILE = __ENV.PROFILE || 'load';
const TITLE_MARKER = '[K6-DEV]';
const WRITER_MARKER = 'k6_loadtest_';

const profiles = {
  smoke: {
    executor: 'constant-vus',
    vus: 1,
    duration: '30s',
  },
  load: {
    executor: 'ramping-vus',
    startVUs: 0,
    stages: [
      { duration: '1m', target: 10 },
      { duration: '0s', target: 30 },
      { duration: '3m', target: 30 },
      { duration: '1m', target: 0 },
    ],
    gracefulRampDown: '10s',
  },
  stress: {
    executor: 'ramping-vus',
    startVUs: 0,
    stages: [
      { duration: '30s', target: 50 },
      { duration: '30s', target: 100 },
      { duration: '3m', target: 100 },
      { duration: '1m', target: 0 },
    ],
    gracefulRampDown: '10s',
  },
};

if (!profiles[PROFILE]) {
  throw new Error(`Unsupported PROFILE: ${PROFILE}`);
}

export const options = {
  scenarios: {
    community_read: profiles[PROFILE],
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<1000'],
    'http_req_duration{name:GET /api/community/settings}': ['p(95)<1000'],
    'http_req_duration{name:GET /api/community/posts?category=RECRUIT}': ['p(95)<1000'],
    'http_req_duration{name:GET /api/community/posts?category=NOTICE}': ['p(95)<1000'],
    'http_req_duration{name:GET /api/community/posts/:postNo}': ['p(95)<1000'],
    'http_req_duration{name:GET /api/community/posts/:postNo/comments}': ['p(95)<1000'],
    checks: ['rate>0.99'],
  },
};

function parseJson(response) {
  try {
    return response.json();
  } catch (_error) {
    return null;
  }
}

function hasJsonEnvelope(body) {
  return body !== null
    && typeof body === 'object'
    && !Array.isArray(body)
    && typeof body.message === 'string'
    && Object.prototype.hasOwnProperty.call(body, 'data');
}

function isSettingsData(data) {
  return data !== null
    && typeof data === 'object'
    && typeof data.visibleToUsers === 'boolean'
    && typeof data.noticeDisplayCount === 'number';
}

function isPostPageData(data) {
  return data !== null
    && typeof data === 'object'
    && Array.isArray(data.posts)
    && typeof data.totalPages === 'number';
}

function isSyntheticPost(post) {
  return post
    && typeof post.no === 'number'
    && typeof post.title === 'string'
    && post.title.startsWith(TITLE_MARKER)
    && typeof post.writerId === 'string'
    && post.writerId.startsWith(WRITER_MARKER);
}

function getList(category, page = 0, size = 20) {
  return http.get(
    `${BASE_URL}/api/community/posts?category=${category}&page=${page}&size=${size}`,
    { tags: { name: `GET /api/community/posts?category=${category}` } },
  );
}

export function setup() {
  const settingsResponse = http.get(`${BASE_URL}/api/community/settings`, {
    tags: { name: 'GET /api/community/settings [setup]' },
  });
  const settingsBody = parseJson(settingsResponse);
  const settingsReady = check(settingsResponse, {
    'setup: settings returns 200': (response) => response.status === 200,
    'setup: settings has JSON envelope': () => hasJsonEnvelope(settingsBody),
    'setup: settings data has expected structure': () => isSettingsData(settingsBody?.data),
    'setup: community is publicly visible': () => settingsBody?.data?.visibleToUsers === true,
  });

  const recruitResponse = getList('RECRUIT', 0, 50);
  const noticeResponse = getList('NOTICE', 0, 50);
  const recruitBody = parseJson(recruitResponse);
  const noticeBody = parseJson(noticeResponse);
  const recruitPosts = recruitBody?.data?.posts || [];
  const noticePosts = noticeBody?.data?.posts || [];
  const syntheticRecruitPosts = recruitPosts.filter(isSyntheticPost);
  const syntheticNoticePosts = noticePosts.filter(isSyntheticPost);

  const dataReady = check(null, {
    'setup: RECRUIT list returns 200': () => recruitResponse.status === 200,
    'setup: RECRUIT list has JSON envelope': () => hasJsonEnvelope(recruitBody),
    'setup: RECRUIT list data has posts array': () => isPostPageData(recruitBody?.data),
    'setup: NOTICE list returns 200': () => noticeResponse.status === 200,
    'setup: NOTICE list has JSON envelope': () => hasJsonEnvelope(noticeBody),
    'setup: NOTICE list data has posts array': () => isPostPageData(noticeBody?.data),
    'setup: synthetic RECRUIT data exists': () => syntheticRecruitPosts.length > 0,
    'setup: synthetic NOTICE data exists': () => syntheticNoticePosts.length > 0,
  });

  if (!settingsReady || !dataReady) {
    fail('Setup failed. Confirm public community settings and run seed-community.sql first.');
  }

  return {
    postIds: [...syntheticRecruitPosts, ...syntheticNoticePosts].map((post) => post.no),
    recruitPages: Math.max(1, recruitBody.data.totalPages),
    noticePages: Math.max(1, noticeBody.data.totalPages),
  };
}

function randomInt(maxExclusive) {
  return Math.floor(Math.random() * maxExclusive);
}

function checkGet(response, label, validateData) {
  const body = parseJson(response);

  check(response, {
    [`${label}: status is 200`]: (result) => result.status === 200,
    [`${label}: has JSON envelope`]: () => hasJsonEnvelope(body),
    [`${label}: data has expected structure`]: () => hasJsonEnvelope(body) && validateData(body.data),
  });
}

export default function (data) {
  const flow = Math.random();

  if (flow < 0.1) {
    const response = http.get(`${BASE_URL}/api/community/settings`, {
      tags: { name: 'GET /api/community/settings' },
    });
    checkGet(response, 'settings', isSettingsData);
  } else if (flow < 0.4) {
    const response = getList('RECRUIT', randomInt(data.recruitPages), 20);
    checkGet(response, 'RECRUIT list', isPostPageData);
  } else if (flow < 0.5) {
    const response = getList('NOTICE', randomInt(data.noticePages), 20);
    checkGet(response, 'NOTICE list', isPostPageData);
  } else if (flow < 0.8) {
    const postNo = data.postIds[randomInt(data.postIds.length)];
    const response = http.get(`${BASE_URL}/api/community/posts/${postNo}`, {
      tags: { name: 'GET /api/community/posts/:postNo' },
    });
    checkGet(response, 'post detail', (responseData) => (
      responseData !== null
      && typeof responseData === 'object'
      && responseData.no === postNo
    ));
  } else {
    const postNo = data.postIds[randomInt(data.postIds.length)];
    const response = http.get(`${BASE_URL}/api/community/posts/${postNo}/comments`, {
      tags: { name: 'GET /api/community/posts/:postNo/comments' },
    });
    checkGet(response, 'comments', (responseData) => (
      Array.isArray(responseData)
      && responseData.every((comment) => comment?.postNo === postNo)
    ));
  }

  sleep(1);
}
