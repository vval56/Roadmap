import { useEffect, useMemo, useRef, useState } from 'react';
import { api, API_BASE_URL, ApiError } from './api';

const ITEM_STATUSES = ['PLANNED', 'IN_PROGRESS', 'DONE'];
const CATALOG_PREFIX = 'Roadmap: ';
const ADMIN_EMAILS = new Set(['vladislav@example.com']);

const DEMO_USERS = [
  { id: 1, firstName: 'Demo', lastName: 'Viewer', email: 'demo.viewer@roadmap.local' }
];

const DEMO_TAG_NAMES = [
  'spring', 'sql', 'java', 'python', 'math', 'machine-learning',
  'deep-learning', 'frontend', 'javascript', 'react', 'devops',
  'docker', 'kubernetes', 'security', 'cloud', 'linux', 'git',
  'data-engineering', 'etl', 'kafka', 'spark', 'airflow', 'nosql',
  'microservices', 'testing', 'android', 'ios', 'swift', 'kotlin',
  'mobile', 'networking', 'cryptography', 'system-design', 'architecture',
  'product', 'analytics', 'observability', 'ci-cd', 'mlops', 'nlp', 'cv'
];

const DEMO_TAGS = DEMO_TAG_NAMES.map((name, index) => ({
  id: 101 + index,
  name
}));

const DEMO_CATALOG_SEEDS = [
  {
    title: 'Machine Learning',
    description: 'Large roadmap from foundations to production ML systems.',
    tagPool: ['machine-learning', 'python', 'math', 'deep-learning', 'mlops', 'analytics', 'cloud', 'product'],
    steps: [
      'Orientation and Learning Plan',
      'Python Foundations',
      'Git, CLI, and Environment Setup',
      'NumPy and Vectorized Computing',
      'Pandas for Data Analysis',
      'SQL for Analytical Workloads',
      'Data Visualization',
      'Linear Algebra Essentials',
      'Calculus for Optimization',
      'Probability and Statistics',
      'Feature Engineering Basics',
      'Train/Validation/Test Strategy',
      'Linear Regression',
      'Logistic Regression',
      'Model Metrics and Thresholds',
      'Decision Trees',
      'Random Forest and Bagging',
      'Gradient Boosting',
      'Hyperparameter Optimization',
      'Unsupervised Learning',
      'Pipelines and Reproducibility',
      'Introduction to Neural Networks',
      'Backpropagation and Optimization',
      'PyTorch Fundamentals',
      'Regularization Techniques',
      'Computer Vision Fundamentals',
      'CNN Architectures',
      'Natural Language Processing Basics',
      'Transformer Architecture',
      'Time Series Forecasting',
      'Experiment Tracking',
      'Data Versioning',
      'Model Packaging',
      'Serving Inference API',
      'Batch and Streaming Inference',
      'Model Monitoring',
      'Feature Store Concepts',
      'CI/CD for ML',
      'Kubernetes Deployment',
      'Responsible AI',
      'A/B Testing for ML Products',
      'Production Incident Playbook',
      'Capstone: End-to-End ML System'
    ]
  },
  {
    title: 'Data Engineering',
    description: 'Reliable pipelines, warehouses, and streaming platforms.',
    tagPool: ['data-engineering', 'sql', 'python', 'etl', 'kafka', 'spark', 'airflow', 'cloud'],
    steps: [
      'Linux and Shell Basics',
      'Python for ETL',
      'SQL Advanced Patterns',
      'Data Modeling Fundamentals',
      'Batch Pipeline Design',
      'Airflow Orchestration',
      'Distributed Processing with Spark',
      'Data Lake Fundamentals',
      'Warehouse Fundamentals',
      'Streaming Concepts',
      'Kafka in Practice',
      'Change Data Capture',
      'Data Quality Checks',
      'Observability for Pipelines',
      'Containerized Jobs',
      'Infrastructure as Code',
      'Data Security and Governance',
      'Cost Optimization',
      'CI/CD for Pipelines',
      'Capstone: Streaming + Batch Platform'
    ]
  },
  {
    title: 'Backend Engineering',
    description: 'Scalable backend services, APIs, and reliability practices.',
    tagPool: ['java', 'spring', 'sql', 'microservices', 'security', 'testing', 'cloud', 'devops'],
    steps: [
      'Java Core and OOP',
      'Collections and Concurrency',
      'Spring Boot Basics',
      'REST API Design',
      'Validation and Error Handling',
      'SQL and Indexing',
      'JPA and Transactions',
      'Caching Strategies',
      'Messaging and Async Processing',
      'Authentication and Authorization',
      'Testing Pyramid',
      'API Documentation',
      'Docker for Backend',
      'Observability',
      'System Reliability',
      'Horizontal Scaling',
      'CI/CD',
      'Capstone: Production Backend Service'
    ]
  },
  {
    title: 'Frontend Engineering',
    description: 'From browser fundamentals to scalable SPA architecture.',
    tagPool: ['frontend', 'javascript', 'react', 'testing', 'product', 'security', 'ci-cd'],
    steps: [
      'HTML and Semantic Layout',
      'CSS Foundations',
      'JavaScript Core',
      'TypeScript Basics',
      'React Fundamentals',
      'Routing and Navigation',
      'State Management',
      'Forms and Validation',
      'Performance Optimization',
      'Testing Frontend',
      'Design System',
      'Accessibility',
      'Frontend Security',
      'Monitoring and RUM',
      'Build and Bundling',
      'Capstone: Production SPA'
    ]
  },
  {
    title: 'DevOps Platform',
    description: 'Automate delivery and operate systems with high reliability.',
    tagPool: ['devops', 'linux', 'networking', 'docker', 'kubernetes', 'cloud', 'ci-cd', 'observability'],
    steps: [
      'Linux Administration',
      'Networking Fundamentals',
      'Bash and Automation',
      'Git Workflows',
      'Docker',
      'Kubernetes Basics',
      'Kubernetes Operations',
      'Ingress and API Gateways',
      'CI/CD Pipelines',
      'Infrastructure as Code',
      'Cloud Fundamentals',
      'Observability Stack',
      'SRE Practices',
      'Security Hardening',
      'Backup and Disaster Recovery',
      'Cost and Capacity Management',
      'Incident Response',
      'Capstone: Production DevOps Stack'
    ]
  },
  {
    title: 'Cybersecurity',
    description: 'Threat modeling, defense, and secure operations.',
    tagPool: ['security', 'networking', 'linux', 'cryptography', 'cloud', 'devops', 'testing'],
    steps: [
      'Security Fundamentals',
      'Networking for Security',
      'Operating System Security',
      'Identity and Access Management',
      'Cryptography Basics',
      'Secure Development Lifecycle',
      'Web Security',
      'API Security',
      'Cloud Security',
      'Container Security',
      'SIEM and Log Analysis',
      'Threat Modeling',
      'Vulnerability Management',
      'Incident Response',
      'Penetration Testing Basics',
      'Capstone: Security Program'
    ]
  },
  {
    title: 'Mobile Engineering',
    description: 'Native and cross-platform path to production mobile apps.',
    tagPool: ['mobile', 'android', 'ios', 'kotlin', 'swift', 'frontend', 'ci-cd', 'security'],
    steps: [
      'Mobile Architecture Basics',
      'Kotlin Essentials',
      'Android UI and Lifecycle',
      'Swift Essentials',
      'iOS UI and Lifecycle',
      'API Integration',
      'Persistence on Device',
      'Authentication on Mobile',
      'Performance and Battery',
      'Push Notifications',
      'Mobile Testing',
      'App Store Delivery',
      'Crash Monitoring',
      'Capstone: Production Mobile App'
    ]
  },
  {
    title: 'QA Automation',
    description: 'From manual testing to full automated quality pipeline.',
    tagPool: ['testing', 'java', 'javascript', 'microservices', 'frontend', 'ci-cd', 'security'],
    steps: [
      'Testing Fundamentals',
      'Test Case Design',
      'API Testing Basics',
      'UI Testing Basics',
      'Java for Test Automation',
      'Selenium and WebDriver',
      'Playwright',
      'Contract Testing',
      'Performance Testing',
      'Security Testing Basics',
      'Test Data Management',
      'CI Integration for Tests',
      'Flaky Test Reduction',
      'Capstone: Full QA Pipeline'
    ]
  },
  {
    title: 'System Design',
    description: 'Design high-load distributed services with clear trade-offs.',
    tagPool: ['system-design', 'architecture', 'analytics', 'kafka', 'networking', 'security', 'cloud'],
    steps: [
      'Requirements and Constraints',
      'Capacity Estimation',
      'Data Modeling for Scale',
      'Caching Layers',
      'Load Balancing',
      'Message Queues and Streams',
      'Database Sharding',
      'NoSQL Patterns',
      'Observability Architecture',
      'Reliability and Resilience',
      'Security by Design',
      'Global Deployment',
      'Design Review Practice',
      'Capstone: Large-Scale Service Design'
    ]
  },
  {
    title: 'Product Analytics',
    description: 'Measure product usage, growth, and experiment impact.',
    tagPool: ['product', 'analytics', 'sql', 'machine-learning', 'testing'],
    steps: [
      'Analytics Foundations',
      'Event Taxonomy',
      'SQL for Product Metrics',
      'Dashboard Design',
      'A/B Testing Fundamentals',
      'Attribution Models',
      'User Segmentation',
      'Forecasting Metrics',
      'Instrumentation QA',
      'Data Storytelling',
      'Decision Frameworks',
      'Capstone: Product Insights Program'
    ]
  }
];

function buildDemoCatalogData() {
  const tagIdByName = new Map(DEMO_TAGS.map((tag) => [tag.name, tag.id]));
  const roadmaps = [];
  const items = [];
  let roadmapId = 9001;
  let itemId = 10001;

  for (const seed of DEMO_CATALOG_SEEDS) {
    const currentRoadmapId = roadmapId;
    roadmapId += 1;

    roadmaps.push({
      id: currentRoadmapId,
      title: `${CATALOG_PREFIX}${seed.title}`,
      description: seed.description,
      ownerId: 1
    });

    let parentItemId = null;

    for (let index = 0; index < seed.steps.length; index += 1) {
      const stepTitle = seed.steps[index];
      const status = index === 0 ? 'DONE' : index === 1 ? 'IN_PROGRESS' : 'PLANNED';
      const primaryTag = seed.tagPool[index % seed.tagPool.length];
      const secondaryTag = seed.tagPool[(index + 2) % seed.tagPool.length];

      const tagIds = [primaryTag, secondaryTag]
        .map((tagName) => tagIdByName.get(tagName))
        .filter((tagId) => typeof tagId === 'number');

      const currentItemId = itemId;
      itemId += 1;

      items.push({
        id: currentItemId,
        roadMapId: currentRoadmapId,
        title: stepTitle,
        details: `Topic focus: ${stepTitle}. Includes checkpoints and practical mini-projects.`,
        status,
        parentItemId,
        tagIds
      });

      parentItemId = currentItemId;
    }
  }

  return { roadmaps, items };
}

const { roadmaps: DEMO_ROADMAPS, items: DEMO_ITEMS } = buildDemoCatalogData();

const TAG_RESOURCE_LINKS = {
  java: [
    { title: 'Java Core Documentation', url: 'https://docs.oracle.com/javase/tutorial/' },
    { title: 'Roadmap Java', url: 'https://roadmap.sh/java' }
  ],
  spring: [
    { title: 'Spring Guides', url: 'https://spring.io/guides' },
    { title: 'Spring Boot Reference', url: 'https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/' }
  ],
  sql: [
    { title: 'SQLBolt Practice', url: 'https://sqlbolt.com/' },
    { title: 'PostgreSQL Tutorial', url: 'https://www.postgresqltutorial.com/' }
  ],
  python: [
    { title: 'Python Docs', url: 'https://docs.python.org/3/tutorial/' },
    { title: 'Roadmap Python', url: 'https://roadmap.sh/python' }
  ],
  react: [
    { title: 'React Learn', url: 'https://react.dev/learn' },
    { title: 'Roadmap React', url: 'https://roadmap.sh/react' }
  ],
  javascript: [
    { title: 'MDN JavaScript Guide', url: 'https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide' },
    { title: 'Roadmap JavaScript', url: 'https://roadmap.sh/javascript' }
  ],
  docker: [
    { title: 'Docker Get Started', url: 'https://docs.docker.com/get-started/' },
    { title: 'Roadmap Docker', url: 'https://roadmap.sh/docker' }
  ],
  kubernetes: [
    { title: 'Kubernetes Tutorials', url: 'https://kubernetes.io/docs/tutorials/' },
    { title: 'Roadmap Kubernetes', url: 'https://roadmap.sh/kubernetes' }
  ],
  'machine-learning': [
    { title: 'Google ML Crash Course', url: 'https://developers.google.com/machine-learning/crash-course' },
    { title: 'Roadmap Machine Learning', url: 'https://roadmap.sh/ai-data-scientist' }
  ],
  'deep-learning': [
    { title: 'DeepLearning.AI Courses', url: 'https://www.deeplearning.ai/courses/' },
    { title: 'FastAI Practical DL', url: 'https://course.fast.ai/' }
  ],
  'data-engineering': [
    { title: 'Roadmap Data Engineer', url: 'https://roadmap.sh/data-engineer' },
    { title: 'Data Engineering Zoomcamp', url: 'https://github.com/DataTalksClub/data-engineering-zoomcamp' }
  ],
  devops: [
    { title: 'Roadmap DevOps', url: 'https://roadmap.sh/devops' },
    { title: 'AWS DevOps Learning Plan', url: 'https://explore.skillbuilder.aws/learn/public/learning_plan/view/1639/devops-engineer-learning-plan' }
  ],
  security: [
    { title: 'OWASP Top 10', url: 'https://owasp.org/www-project-top-ten/' },
    { title: 'Roadmap Cyber Security', url: 'https://roadmap.sh/cyber-security' }
  ],
  frontend: [
    { title: 'Roadmap Frontend', url: 'https://roadmap.sh/frontend' },
    { title: 'MDN Learn Web Development', url: 'https://developer.mozilla.org/en-US/docs/Learn' }
  ],
  backend: [
    { title: 'Roadmap Backend', url: 'https://roadmap.sh/backend' }
  ]
};

function buildStepLearningLinks(item, tagsById) {
  if (!item) {
    return [];
  }

  const links = [];
  const seen = new Set();
  const tagNames = (item.tagIds || [])
    .map((tagId) => tagsById.get(tagId)?.name)
    .filter(Boolean);

  for (const tagName of tagNames) {
    const resources = TAG_RESOURCE_LINKS[tagName.toLowerCase()];
    if (!resources) {
      continue;
    }
    for (const resource of resources) {
      if (seen.has(resource.url)) {
        continue;
      }
      links.push(resource);
      seen.add(resource.url);
      if (links.length >= 4) {
        break;
      }
    }
    if (links.length >= 4) {
      break;
    }
  }

  const titleQuery = encodeURIComponent(`${item.title} tutorial`);
  const deepQuery = encodeURIComponent(`${item.title} ${tagNames.join(' ')} full course`);

  const fallbackLinks = [
    { title: `YouTube: ${item.title}`, url: `https://www.youtube.com/results?search_query=${titleQuery}` },
    { title: `Course Search: ${item.title}`, url: `https://www.google.com/search?q=${deepQuery}` }
  ];

  for (const fallback of fallbackLinks) {
    if (seen.has(fallback.url)) {
      continue;
    }
    links.push(fallback);
    seen.add(fallback.url);
  }

  return links.slice(0, 6);
}

function normalizeItem(raw) {
  const normalizedStatus = ITEM_STATUSES.includes(raw.status) ? raw.status : 'PLANNED';
  return {
    ...raw,
    status: normalizedStatus,
    details: raw.details || '',
    parentItemId: raw.parentItemId ?? null,
    tagIds: Array.isArray(raw.tagIds) ? raw.tagIds.map(Number) : []
  };
}

function normalizeCatalogTitle(title) {
  if (!title) {
    return 'Untitled';
  }
  if (title.startsWith(CATALOG_PREFIX)) {
    return title.slice(CATALOG_PREFIX.length).trim();
  }
  return title;
}

function formatStatus(status) {
  switch (status) {
    case 'IN_PROGRESS':
      return 'In progress';
    case 'DONE':
      return 'Done';
    default:
      return 'Planned';
  }
}

function statusClass(status) {
  switch (status) {
    case 'DONE':
      return 'done';
    case 'IN_PROGRESS':
      return 'in-progress';
    default:
      return 'planned';
  }
}

function isStatusTransitionAllowed(currentStatus, nextStatus) {
  if (currentStatus === 'DONE') {
    return nextStatus === 'DONE';
  }
  if (currentStatus === 'IN_PROGRESS') {
    return nextStatus === 'IN_PROGRESS' || nextStatus === 'DONE';
  }
  return ITEM_STATUSES.includes(nextStatus);
}

function getAllowedStatuses(currentStatus) {
  if (currentStatus === 'DONE') {
    return ['DONE'];
  }
  if (currentStatus === 'IN_PROGRESS') {
    return ['IN_PROGRESS', 'DONE'];
  }
  return [...ITEM_STATUSES];
}

function selectRoadmapItemsFromPool(roadmapId, itemsPool) {
  return itemsPool
    .filter((item) => item.roadMapId === roadmapId)
    .sort((a, b) => a.id - b.id);
}

function buildVerticalGraph(items) {
  if (items.length === 0) {
    return {
      width: 760,
      height: 360,
      nodes: [],
      edges: []
    };
  }

  const byId = new Map(items.map((item) => [item.id, item]));
  const depthMemo = new Map();

  function depthOf(itemId, visiting = new Set()) {
    if (depthMemo.has(itemId)) {
      return depthMemo.get(itemId);
    }
    if (visiting.has(itemId)) {
      return 0;
    }

    const current = byId.get(itemId);
    if (!current) {
      return 0;
    }

    const parentId = current.parentItemId;
    if (!parentId || !byId.has(parentId)) {
      depthMemo.set(itemId, 0);
      return 0;
    }

    visiting.add(itemId);
    const depth = depthOf(parentId, visiting) + 1;
    visiting.delete(itemId);
    depthMemo.set(itemId, depth);
    return depth;
  }

  const levels = new Map();
  for (const item of items) {
    const depth = depthOf(item.id);
    if (!levels.has(depth)) {
      levels.set(depth, []);
    }
    levels.get(depth).push(item);
  }

  const orderedDepths = [...levels.keys()].sort((a, b) => a - b);
  for (const depth of orderedDepths) {
    levels.get(depth).sort((a, b) => a.id - b.id);
  }

  const compactMode = typeof window !== 'undefined' && window.innerWidth <= 720;
  const nodeWidth = compactMode ? 190 : 230;
  const nodeHeight = compactMode ? 78 : 84;
  const gapX = compactMode ? 22 : 44;
  const gapY = compactMode ? 64 : 90;
  const paddingX = compactMode ? 16 : 30;
  const paddingY = compactMode ? 20 : 26;

  const maxColumns = Math.max(...orderedDepths.map((depth) => levels.get(depth).length));
  const width = Math.max(
    compactMode ? 340 : 760,
    paddingX * 2 + maxColumns * nodeWidth + Math.max(maxColumns - 1, 0) * gapX
  );
  const height = Math.max(
    compactMode ? 280 : 360,
    paddingY * 2 + orderedDepths.length * nodeHeight + Math.max(orderedDepths.length - 1, 0) * gapY
  );

  const positionsById = new Map();
  const nodes = [];

  for (const depth of orderedDepths) {
    const levelItems = levels.get(depth);
    const levelWidth = levelItems.length * nodeWidth + Math.max(levelItems.length - 1, 0) * gapX;
    const startX = paddingX + Math.max((width - paddingX * 2 - levelWidth) / 2, 0);

    levelItems.forEach((item, columnIndex) => {
      const x = startX + columnIndex * (nodeWidth + gapX);
      const y = paddingY + depth * (nodeHeight + gapY);

      positionsById.set(item.id, { x, y, width: nodeWidth, height: nodeHeight });
      nodes.push({ item, x, y, width: nodeWidth, height: nodeHeight });
    });
  }

  const edges = [];
  for (const item of items) {
    if (!item.parentItemId) {
      continue;
    }

    const from = positionsById.get(item.parentItemId);
    const to = positionsById.get(item.id);
    if (!from || !to) {
      continue;
    }

    const sx = from.x + from.width / 2;
    const sy = from.y + from.height;
    const tx = to.x + to.width / 2;
    const ty = to.y;

    const controlY = sy + (ty - sy) * 0.48;

    edges.push({
      id: `${item.parentItemId}-${item.id}`,
      path: `M ${sx} ${sy} C ${sx} ${controlY}, ${tx} ${controlY}, ${tx} ${ty}`
    });
  }

  return { width, height, nodes, edges };
}

export default function App() {
  const roadmapLoadSeqRef = useRef(0);

  const [users, setUsers] = useState([]);
  const [tags, setTags] = useState([]);
  const [roadmaps, setRoadmaps] = useState([]);
  const [allItems, setAllItems] = useState([]);

  const [selectedRoadmapId, setSelectedRoadmapId] = useState(null);
  const [roadmapItems, setRoadmapItems] = useState([]);
  const [selectedItemId, setSelectedItemId] = useState(null);
  const [statusDraft, setStatusDraft] = useState('PLANNED');

  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [activePage, setActivePage] = useState('viewer');
  const [showLoginPanel, setShowLoginPanel] = useState(false);
  const [currentUser, setCurrentUser] = useState(null);
  const [loginEmail, setLoginEmail] = useState('');
  const [authInfo, setAuthInfo] = useState('');
  const [dataSource, setDataSource] = useState('api');

  const [adminRoadmapTitle, setAdminRoadmapTitle] = useState('');
  const [adminRoadmapDescription, setAdminRoadmapDescription] = useState('');
  const [adminEditRoadmapTitle, setAdminEditRoadmapTitle] = useState('');
  const [adminEditRoadmapDescription, setAdminEditRoadmapDescription] = useState('');
  const [adminStepTitle, setAdminStepTitle] = useState('');
  const [adminStepDetails, setAdminStepDetails] = useState('');
  const [adminStepStatus, setAdminStepStatus] = useState('PLANNED');
  const [adminStepParentId, setAdminStepParentId] = useState('');
  const [adminStepTagIds, setAdminStepTagIds] = useState([]);
  const [adminEditStepId, setAdminEditStepId] = useState('');
  const [adminEditStepTitle, setAdminEditStepTitle] = useState('');
  const [adminEditStepDetails, setAdminEditStepDetails] = useState('');
  const [adminEditStepStatus, setAdminEditStepStatus] = useState('PLANNED');
  const [adminEditStepParentId, setAdminEditStepParentId] = useState('');
  const [adminEditStepTagIds, setAdminEditStepTagIds] = useState([]);
  const [adminBulkStepsText, setAdminBulkStepsText] = useState('');
  const [adminBulkStatus, setAdminBulkStatus] = useState('PLANNED');
  const [adminBulkParentId, setAdminBulkParentId] = useState('');
  const [adminBulkTagIds, setAdminBulkTagIds] = useState([]);
  const [adminBulkChain, setAdminBulkChain] = useState(true);

  const [loadingApp, setLoadingApp] = useState(true);
  const [loadingItems, setLoadingItems] = useState(false);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const [toast, setToast] = useState(null);

  const tagsById = useMemo(() => {
    const map = new Map();
    for (const tag of tags) {
      map.set(tag.id, tag);
    }
    return map;
  }, [tags]);

  const selectedRoadmap = useMemo(
    () => roadmaps.find((roadmap) => roadmap.id === selectedRoadmapId) || null,
    [roadmaps, selectedRoadmapId]
  );

  const selectedItem = useMemo(
    () => roadmapItems.find((item) => item.id === selectedItemId) || null,
    [roadmapItems, selectedItemId]
  );

  const adminEditableStep = useMemo(() => {
    const stepId = Number(adminEditStepId);
    if (!stepId) {
      return null;
    }
    return roadmapItems.find((item) => item.id === stepId) || null;
  }, [roadmapItems, adminEditStepId]);

  const isAdminUser = useMemo(() => {
    const email = (currentUser?.email || '').trim().toLowerCase();
    return email.length > 0 && ADMIN_EMAILS.has(email);
  }, [currentUser]);

  const searchableRoadmaps = useMemo(() => roadmaps, [roadmaps]);

  const filteredItems = useMemo(() => roadmapItems, [roadmapItems]);

  const graph = useMemo(() => buildVerticalGraph(filteredItems), [filteredItems]);
  const stepLearningLinks = useMemo(
    () => buildStepLearningLinks(selectedItem, tagsById),
    [selectedItem, tagsById]
  );

  function showError(message) {
    setError(message);
  }

  function clearMessages() {
    setError('');
  }

  function showToast(type, message) {
    setToast({ type, message });
  }

  function ensureCatalogTitle(title) {
    const normalized = (title || '').trim();
    if (!normalized) {
      return '';
    }
    return normalized.startsWith(CATALOG_PREFIX) ? normalized : `${CATALOG_PREFIX}${normalized}`;
  }

  async function loadCatalogRoadmaps() {
    try {
      return await api.get('/roadmaps/catalog');
    } catch (loadError) {
      if (loadError instanceof ApiError) {
        return api.get('/roadmaps');
      }
      throw loadError;
    }
  }

  async function refreshAllItemsFromApi(roadMapId = null) {
    const endpoint = roadMapId ? `/roadmap-items?roadMapId=${roadMapId}` : '/roadmap-items';
    const apiItems = await api.get(endpoint);
    const normalizedItems = apiItems.map(normalizeItem);
    const filtered = roadMapId
      ? normalizedItems.filter((item) => Number(item.roadMapId) === Number(roadMapId))
      : normalizedItems;
    return filtered.sort((a, b) => a.id - b.id);
  }

  function applySnapshot(snapshot, preferredRoadmapId = null) {
    setDataSource(snapshot.source);
    setUsers(snapshot.users);
    setTags(snapshot.tags);
    setRoadmaps(snapshot.roadmaps);
    setAllItems(snapshot.items);

    const chosenRoadmapId =
      preferredRoadmapId && snapshot.roadmaps.some((roadmap) => roadmap.id === preferredRoadmapId)
        ? preferredRoadmapId
        : null;

    setSelectedRoadmapId(chosenRoadmapId);
    const items = chosenRoadmapId
      ? selectRoadmapItemsFromPool(chosenRoadmapId, snapshot.items)
      : [];

    setRoadmapItems(items);
    setSelectedItemId(null);
    setStatusDraft('PLANNED');
  }

  function loadFallbackCatalog(preferredRoadmapId = null) {
    const demoItems = DEMO_ITEMS.map(normalizeItem);
    applySnapshot(
      {
        source: 'demo',
        users: DEMO_USERS,
        tags: DEMO_TAGS,
        roadmaps: DEMO_ROADMAPS,
        items: demoItems
      },
      preferredRoadmapId
    );

    if (!loginEmail) {
      setLoginEmail(DEMO_USERS[0].email);
    }
  }

  async function bootstrap(preferredRoadmapId = null) {
    setLoadingApp(true);
    clearMessages();

    try {
      const roadmapsData = await loadCatalogRoadmaps();
      const normalizedRoadmaps = Array.isArray(roadmapsData)
        ? roadmapsData.filter((roadmap) => roadmap?.id)
        : [];

      if (normalizedRoadmaps.length === 0) {
        loadFallbackCatalog(preferredRoadmapId);
        return;
      }

      const [usersData, tagsData] = await Promise.all([
        api.get('/users').catch(() => []),
        api.get('/tags').catch(() => [])
      ]);

      applySnapshot(
        {
          source: 'api',
          users: usersData,
          tags: tagsData.length > 0 ? tagsData : DEMO_TAGS,
          roadmaps: normalizedRoadmaps,
          items: []
        },
        preferredRoadmapId
      );

      if (!loginEmail && usersData.length > 0) {
        setLoginEmail(usersData[0].email);
      }
    } catch (loadError) {
      loadFallbackCatalog(preferredRoadmapId);
    } finally {
      setLoadingApp(false);
    }
  }

  useEffect(() => {
    bootstrap();
  }, []);

  useEffect(() => {
    if (!selectedItem) {
      setStatusDraft('PLANNED');
      return;
    }
    setStatusDraft(selectedItem.status || 'PLANNED');
  }, [selectedItem]);

  useEffect(() => {
    if (!toast) {
      return;
    }
    const timerId = window.setTimeout(() => setToast(null), 2200);
    return () => window.clearTimeout(timerId);
  }, [toast]);

  useEffect(() => {
    if (!adminStepParentId) {
      return;
    }
    const existsInCurrentRoadmap = roadmapItems.some((item) => String(item.id) === String(adminStepParentId));
    if (!existsInCurrentRoadmap) {
      setAdminStepParentId('');
    }
  }, [adminStepParentId, roadmapItems]);

  useEffect(() => {
    if (!adminBulkParentId) {
      return;
    }
    const existsInCurrentRoadmap = roadmapItems.some((item) => String(item.id) === String(adminBulkParentId));
    if (!existsInCurrentRoadmap) {
      setAdminBulkParentId('');
    }
  }, [adminBulkParentId, roadmapItems]);

  useEffect(() => {
    if (activePage === 'admin' && !isAdminUser) {
      setActivePage('viewer');
    }
  }, [activePage, isAdminUser]);

  useEffect(() => {
    if (!selectedRoadmap) {
      setAdminEditRoadmapTitle('');
      setAdminEditRoadmapDescription('');
      return;
    }
    setAdminEditRoadmapTitle(normalizeCatalogTitle(selectedRoadmap.title));
    setAdminEditRoadmapDescription(selectedRoadmap.description || '');
  }, [selectedRoadmap]);

  useEffect(() => {
    if (!adminEditStepId) {
      return;
    }
    const existsInCurrentRoadmap = roadmapItems.some((item) => String(item.id) === String(adminEditStepId));
    if (!existsInCurrentRoadmap) {
      setAdminEditStepId('');
    }
  }, [adminEditStepId, roadmapItems]);

  useEffect(() => {
    if (!adminEditableStep) {
      setAdminEditStepTitle('');
      setAdminEditStepDetails('');
      setAdminEditStepStatus('PLANNED');
      setAdminEditStepParentId('');
      setAdminEditStepTagIds([]);
      return;
    }
    setAdminEditStepTitle(adminEditableStep.title || '');
    setAdminEditStepDetails(adminEditableStep.details || '');
    setAdminEditStepStatus(adminEditableStep.status || 'PLANNED');
    setAdminEditStepParentId(adminEditableStep.parentItemId ? String(adminEditableStep.parentItemId) : '');
    setAdminEditStepTagIds(Array.isArray(adminEditableStep.tagIds) ? [...adminEditableStep.tagIds] : []);
  }, [adminEditableStep]);

  useEffect(() => {
    if (!adminEditStepParentId) {
      return;
    }
    const existsInCurrentRoadmap = roadmapItems.some((item) => String(item.id) === String(adminEditStepParentId));
    if (!existsInCurrentRoadmap || String(adminEditStepParentId) === String(adminEditStepId)) {
      setAdminEditStepParentId('');
    }
  }, [adminEditStepParentId, adminEditStepId, roadmapItems]);

  async function selectRoadmap(roadmapId) {
    const numericId = Number(roadmapId);
    if (!numericId) {
      return;
    }
    const requestId = roadmapLoadSeqRef.current + 1;
    roadmapLoadSeqRef.current = requestId;

    setSelectedRoadmapId(numericId);
    setLoadingItems(true);
    setSidebarOpen(false);
    setSelectedItemId(null);
    setStatusDraft('PLANNED');

    try {
      const items =
        dataSource === 'api'
          ? await refreshAllItemsFromApi(numericId)
          : selectRoadmapItemsFromPool(numericId, allItems);
      if (requestId !== roadmapLoadSeqRef.current) {
        return;
      }
      if (dataSource === 'api') {
        setAllItems(items);
      }
      setRoadmapItems(items);
      clearMessages();
    } catch (loadError) {
      if (requestId !== roadmapLoadSeqRef.current) {
        return;
      }
      showError(loadError.message || 'Failed to load roadmap');
      setAllItems([]);
      setRoadmapItems([]);
    } finally {
      if (requestId === roadmapLoadSeqRef.current) {
        setLoadingItems(false);
      }
    }
  }

  function handleAuthButtonClick() {
    if (currentUser) {
      setCurrentUser(null);
      setShowLoginPanel(false);
      setActivePage('viewer');
      setAuthInfo('');
      showToast('info', 'Signed out');
      return;
    }
    setAuthInfo('');
    setShowLoginPanel((prev) => !prev);
  }

  function handleLoginSubmit(event) {
    event.preventDefault();

    const email = loginEmail.trim();
    if (!email) {
      showError('Enter email');
      return;
    }

    if (dataSource === 'api') {
      const user = users.find((entry) => (entry.email || '').toLowerCase() === email.toLowerCase());
      if (!user) {
        showError('User not found');
        return;
      }
      setCurrentUser(user);
    } else {
      setCurrentUser({ id: DEMO_USERS[0].id, email, firstName: 'Demo', lastName: 'User' });
    }

    setAuthInfo('Signed in');
    showToast('success', 'Signed in');
    window.setTimeout(() => {
      setShowLoginPanel(false);
      setAuthInfo('');
    }, 900);
    setError('');
  }

  async function handleAdminCreateRoadmap(event) {
    event.preventDefault();

    if (!isAdminUser) {
      showError('Admin access required');
      return;
    }

    if (!currentUser) {
      showError('Sign in required');
      return;
    }

    const title = ensureCatalogTitle(adminRoadmapTitle);
    if (!title) {
      showError('Enter roadmap title');
      return;
    }

    setBusy(true);
    clearMessages();

    try {
      if (dataSource === 'api') {
        const payload = {
          title,
          description: adminRoadmapDescription.trim() || null,
          ownerId: currentUser.id
        };
        const created = await api.post('/roadmaps', payload);
        const refreshedRoadmaps = await loadCatalogRoadmaps();
        const normalizedRoadmaps = Array.isArray(refreshedRoadmaps)
          ? refreshedRoadmaps.filter((roadmap) => roadmap?.id)
          : [];
        setRoadmaps(normalizedRoadmaps);
        setAdminRoadmapTitle('');
        setAdminRoadmapDescription('');
        showToast('success', 'Roadmap created');
        await selectRoadmap(created.id);
      } else {
        const nextId = roadmaps.reduce((maxId, roadmap) => Math.max(maxId, roadmap.id || 0), 0) + 1;
        const created = {
          id: nextId,
          title,
          description: adminRoadmapDescription.trim() || '',
          ownerId: currentUser.id
        };
        const updatedRoadmaps = [...roadmaps, created];
        setRoadmaps(updatedRoadmaps);
        setAdminRoadmapTitle('');
        setAdminRoadmapDescription('');
        setSelectedRoadmapId(created.id);
        setRoadmapItems([]);
        setSelectedItemId(null);
        setStatusDraft('PLANNED');
        showToast('success', 'Roadmap created');
      }
    } catch (createError) {
      showError(createError.message || 'Failed to create roadmap');
    } finally {
      setBusy(false);
    }
  }

  function handleAdminTagSelect(event) {
    const selected = Array.from(event.target.selectedOptions).map((option) => Number(option.value));
    setAdminStepTagIds(selected);
  }

  function handleAdminBulkTagSelect(event) {
    const selected = Array.from(event.target.selectedOptions).map((option) => Number(option.value));
    setAdminBulkTagIds(selected);
  }

  function handleAdminEditStepTagSelect(event) {
    const selected = Array.from(event.target.selectedOptions).map((option) => Number(option.value));
    setAdminEditStepTagIds(selected);
  }

  async function handleAdminRoadmapSelect(event) {
    const roadmapId = Number(event.target.value);
    setAdminEditStepId('');
    if (!roadmapId) {
      setSelectedRoadmapId(null);
      setRoadmapItems([]);
      setSelectedItemId(null);
      setStatusDraft('PLANNED');
      return;
    }
    await selectRoadmap(roadmapId);
  }

  async function handleAdminUpdateRoadmap(event) {
    event.preventDefault();

    if (!isAdminUser) {
      showError('Admin access required');
      return;
    }
    if (!selectedRoadmap) {
      showError('Select a roadmap first');
      return;
    }

    const title = ensureCatalogTitle(adminEditRoadmapTitle);
    if (!title) {
      showError('Enter roadmap title');
      return;
    }

    setBusy(true);
    clearMessages();

    try {
      if (dataSource === 'api') {
        await api.put(`/roadmaps/${selectedRoadmap.id}`, {
          id: selectedRoadmap.id,
          title,
          description: adminEditRoadmapDescription.trim() || null,
          ownerId: selectedRoadmap.ownerId || currentUser?.id
        });
        const refreshedRoadmaps = await loadCatalogRoadmaps();
        const normalizedRoadmaps = Array.isArray(refreshedRoadmaps)
          ? refreshedRoadmaps.filter((roadmap) => roadmap?.id)
          : [];
        setRoadmaps(normalizedRoadmaps);
      } else {
        setRoadmaps((prevRoadmaps) =>
          prevRoadmaps.map((roadmap) =>
            roadmap.id === selectedRoadmap.id
              ? {
                  ...roadmap,
                  title,
                  description: adminEditRoadmapDescription.trim()
                }
              : roadmap
          )
        );
      }
      showToast('success', 'Roadmap updated');
    } catch (updateError) {
      showError(updateError.message || 'Failed to update roadmap');
    } finally {
      setBusy(false);
    }
  }

  async function handleAdminDeleteRoadmap() {
    if (!isAdminUser) {
      showError('Admin access required');
      return;
    }
    if (!selectedRoadmap) {
      showError('Select a roadmap first');
      return;
    }
    if (!window.confirm(`Delete roadmap "${normalizeCatalogTitle(selectedRoadmap.title)}"?`)) {
      return;
    }

    setBusy(true);
    clearMessages();

    try {
      if (dataSource === 'api') {
        await api.del(`/roadmaps/${selectedRoadmap.id}`);
        const refreshedRoadmaps = await loadCatalogRoadmaps();
        const normalizedRoadmaps = Array.isArray(refreshedRoadmaps)
          ? refreshedRoadmaps.filter((roadmap) => roadmap?.id)
          : [];
        setRoadmaps(normalizedRoadmaps);
      } else {
        setRoadmaps((prevRoadmaps) => prevRoadmaps.filter((roadmap) => roadmap.id !== selectedRoadmap.id));
        setAllItems((prevItems) => prevItems.filter((item) => item.roadMapId !== selectedRoadmap.id));
      }

      setSelectedRoadmapId(null);
      setRoadmapItems([]);
      setSelectedItemId(null);
      setStatusDraft('PLANNED');
      setAdminEditStepId('');
      showToast('success', 'Roadmap deleted');
    } catch (deleteError) {
      showError(deleteError.message || 'Failed to delete roadmap');
    } finally {
      setBusy(false);
    }
  }

  async function handleAdminUpdateStep(event) {
    event.preventDefault();

    if (!isAdminUser) {
      showError('Admin access required');
      return;
    }
    if (!selectedRoadmapId) {
      showError('Select a roadmap first');
      return;
    }
    if (!adminEditableStep) {
      showError('Select a step to edit');
      return;
    }

    const title = adminEditStepTitle.trim();
    if (!title) {
      showError('Enter step title');
      return;
    }

    const parentItemId = adminEditStepParentId ? Number(adminEditStepParentId) : null;
    if (parentItemId && parentItemId === Number(adminEditStepId)) {
      showError('A step cannot be its own parent');
      return;
    }

    const payload = {
      id: adminEditableStep.id,
      roadMapId: selectedRoadmapId,
      title,
      details: adminEditStepDetails.trim() || null,
      status: adminEditStepStatus,
      parentItemId,
      tagIds: [...adminEditStepTagIds]
    };

    setBusy(true);
    clearMessages();

    try {
      if (dataSource === 'api') {
        await api.put(`/roadmap-items/${adminEditableStep.id}`, payload);
        const itemsPool = await refreshAllItemsFromApi(selectedRoadmapId);
        setAllItems(itemsPool);
        setRoadmapItems(itemsPool);
      } else {
        const updatedItems = allItems.map((item) =>
          item.id === adminEditableStep.id
            ? {
                ...item,
                title: payload.title,
                details: payload.details || '',
                status: payload.status,
                parentItemId: payload.parentItemId,
                tagIds: [...payload.tagIds]
              }
            : item
        );
        setAllItems(updatedItems);
        setRoadmapItems(selectRoadmapItemsFromPool(selectedRoadmapId, updatedItems));
      }
      setSelectedItemId(adminEditableStep.id);
      showToast('success', 'Step updated');
    } catch (updateError) {
      showError(updateError.message || 'Failed to update step');
    } finally {
      setBusy(false);
    }
  }

  async function handleAdminDeleteStep() {
    if (!isAdminUser) {
      showError('Admin access required');
      return;
    }
    if (!selectedRoadmapId) {
      showError('Select a roadmap first');
      return;
    }
    if (!adminEditableStep) {
      showError('Select a step to delete');
      return;
    }
    if (!window.confirm(`Delete step "${adminEditableStep.title}"?`)) {
      return;
    }

    setBusy(true);
    clearMessages();

    try {
      if (dataSource === 'api') {
        await api.del(`/roadmap-items/${adminEditableStep.id}`);
        const itemsPool = await refreshAllItemsFromApi(selectedRoadmapId);
        setAllItems(itemsPool);
        setRoadmapItems(itemsPool);
      } else {
        const updatedItems = allItems.filter((item) => item.id !== adminEditableStep.id);
        setAllItems(updatedItems);
        setRoadmapItems(selectRoadmapItemsFromPool(selectedRoadmapId, updatedItems));
      }
      setAdminEditStepId('');
      setSelectedItemId(null);
      showToast('success', 'Step deleted');
    } catch (deleteError) {
      showError(deleteError.message || 'Failed to delete step');
    } finally {
      setBusy(false);
    }
  }

  async function handleAdminCreateStep(event) {
    event.preventDefault();

    if (!isAdminUser) {
      showError('Admin access required');
      return;
    }

    if (!currentUser) {
      showError('Sign in required');
      return;
    }

    if (!selectedRoadmapId) {
      showError('Select a roadmap first');
      return;
    }

    const title = adminStepTitle.trim();
    if (!title) {
      showError('Enter step title');
      return;
    }

    const payload = {
      title,
      details: adminStepDetails.trim() || null,
      status: adminStepStatus,
      parentItemId: adminStepParentId ? Number(adminStepParentId) : null,
      tagIds: [...adminStepTagIds],
      roadMapId: selectedRoadmapId
    };

    setBusy(true);
    clearMessages();

    try {
      if (dataSource === 'api') {
        const created = await api.post('/roadmap-items', payload);
        const itemsPool = await refreshAllItemsFromApi(selectedRoadmapId);
        setAllItems(itemsPool);
        setRoadmapItems(itemsPool);
        setSelectedItemId(created.id);
      } else {
        const nextId = allItems.reduce((maxId, item) => Math.max(maxId, item.id || 0), 0) + 1;
        const created = {
          id: nextId,
          roadMapId: selectedRoadmapId,
          title: payload.title,
          details: payload.details || '',
          status: payload.status,
          parentItemId: payload.parentItemId,
          tagIds: [...payload.tagIds]
        };
        const updatedItems = [...allItems, created];
        setAllItems(updatedItems);
        setRoadmapItems(selectRoadmapItemsFromPool(selectedRoadmapId, updatedItems));
        setSelectedItemId(nextId);
      }

      setAdminStepTitle('');
      setAdminStepDetails('');
      setAdminStepStatus('PLANNED');
      setAdminStepParentId('');
      setAdminStepTagIds([]);
      showToast('success', 'Step added');
    } catch (createError) {
      showError(createError.message || 'Failed to add step');
    } finally {
      setBusy(false);
    }
  }

  function parseBulkSteps(source) {
    return source
      .split('\n')
      .map((line) => line.trim())
      .filter(Boolean)
      .map((line) => {
        const separatorIndex = line.indexOf('|');
        if (separatorIndex === -1) {
          return { title: line, details: '' };
        }
        const title = line.slice(0, separatorIndex).trim();
        const details = line.slice(separatorIndex + 1).trim();
        return { title, details };
      })
      .filter((step) => step.title.length > 0);
  }

  async function handleAdminCreateBulkSteps(event) {
    event.preventDefault();

    if (!isAdminUser) {
      showError('Admin access required');
      return;
    }

    if (!currentUser) {
      showError('Sign in required');
      return;
    }

    if (!selectedRoadmapId) {
      showError('Select a roadmap first');
      return;
    }

    const parsedSteps = parseBulkSteps(adminBulkStepsText);
    if (parsedSteps.length === 0) {
      showError('Add at least one step');
      return;
    }

    setBusy(true);
    clearMessages();

    try {
      if (dataSource === 'api') {
        let parentItemId = adminBulkParentId ? Number(adminBulkParentId) : null;
        let lastCreatedId = null;

        for (const step of parsedSteps) {
          const payload = {
            roadMapId: selectedRoadmapId,
            title: step.title,
            details: step.details || null,
            status: adminBulkStatus,
            parentItemId,
            tagIds: [...adminBulkTagIds]
          };
          const created = await api.post('/roadmap-items', payload);
          lastCreatedId = created.id;
          if (adminBulkChain) {
            parentItemId = created.id;
          }
        }

        const itemsPool = await refreshAllItemsFromApi(selectedRoadmapId);
        setAllItems(itemsPool);
        setRoadmapItems(itemsPool);
        if (lastCreatedId) {
          setSelectedItemId(lastCreatedId);
        }
      } else {
        let nextId = allItems.reduce((maxId, item) => Math.max(maxId, item.id || 0), 0) + 1;
        let parentItemId = adminBulkParentId ? Number(adminBulkParentId) : null;
        const createdItems = [];

        for (const step of parsedSteps) {
          const created = {
            id: nextId,
            roadMapId: selectedRoadmapId,
            title: step.title,
            details: step.details || '',
            status: adminBulkStatus,
            parentItemId,
            tagIds: [...adminBulkTagIds]
          };
          createdItems.push(created);
          nextId += 1;
          if (adminBulkChain) {
            parentItemId = created.id;
          }
        }

        const updatedItems = [...allItems, ...createdItems];
        setAllItems(updatedItems);
        setRoadmapItems(selectRoadmapItemsFromPool(selectedRoadmapId, updatedItems));
        setSelectedItemId(createdItems[createdItems.length - 1]?.id || null);
      }

      setAdminBulkStepsText('');
      setAdminBulkStatus('PLANNED');
      setAdminBulkParentId('');
      setAdminBulkTagIds([]);
      setAdminBulkChain(true);
      showToast('success', `Steps added: ${parsedSteps.length}`);
    } catch (createError) {
      showError(createError.message || 'Failed to add steps');
    } finally {
      setBusy(false);
    }
  }

  function handleGoHome() {
    setActivePage('viewer');
    setSidebarOpen(false);
    setSelectedRoadmapId(null);
    setRoadmapItems([]);
    setSelectedItemId(null);
    setStatusDraft('PLANNED');
  }

  function handleStatusDraftChange(nextStatus) {
    if (!currentUser) {
      showError('Sign in required');
      setShowLoginPanel(true);
      return;
    }

    const currentStatus = selectedItem?.status || 'PLANNED';
    if (!isStatusTransitionAllowed(currentStatus, nextStatus)) {
      showError('Invalid status transition');
      return;
    }
    setStatusDraft(nextStatus);
  }

  async function saveStatusForSelectedItem() {
    if (!selectedItem) {
      return;
    }

    if (!currentUser) {
      showError('Sign in required');
      setShowLoginPanel(true);
      return;
    }

    setBusy(true);
    clearMessages();

    try {
      const currentStatus = selectedItem.status || 'PLANNED';
      if (!isStatusTransitionAllowed(currentStatus, statusDraft)) {
        showError('Invalid status transition');
        setStatusDraft(currentStatus);
        return;
      }

      if (dataSource === 'api') {
        const payload = {
          id: selectedItem.id,
          roadMapId: selectedItem.roadMapId,
          title: selectedItem.title,
          details: selectedItem.details,
          status: statusDraft,
          parentItemId: selectedItem.parentItemId,
          tagIds: [...selectedItem.tagIds]
        };

        await api.put(`/roadmap-items/${selectedItem.id}`, payload);
        const itemsPool = await refreshAllItemsFromApi(selectedRoadmapId);
        setAllItems(itemsPool);
        setRoadmapItems(itemsPool);
      } else {
        const updatedAllItems = allItems.map((item) =>
          item.id === selectedItem.id ? { ...item, status: statusDraft } : item
        );
        setAllItems(updatedAllItems);
        setRoadmapItems(selectRoadmapItemsFromPool(selectedRoadmapId, updatedAllItems));
      }
    } catch (saveError) {
      showError(saveError.message || 'Failed to update status');
    } finally {
      setBusy(false);
    }
  }

  if (loadingApp) {
    return (
      <div className="it-roadmap-app">
        <div className="loader">Loading from {API_BASE_URL}...</div>
      </div>
    );
  }

  return (
    <div className="it-roadmap-app">
      <header className="topbar">
        <button
          className="menu-btn"
          onClick={() => {
            if (activePage === 'admin') {
              setActivePage('viewer');
              return;
            }
            setSidebarOpen(true);
          }}
        >
          {activePage === 'admin' ? 'Catalog' : 'Menu'}
        </button>
        <h1>IT Roadmap</h1>
        <div className="topbar-spacer" />
        <button className="menu-btn" onClick={handleGoHome}>
          Home
        </button>
        {isAdminUser ? (
          <button
            className="menu-btn"
            onClick={() => setActivePage((prevPage) => (prevPage === 'admin' ? 'viewer' : 'admin'))}
          >
            {activePage === 'admin' ? 'Viewer' : 'Admin'}
          </button>
        ) : null}
        <button className="auth-btn" onClick={handleAuthButtonClick}>
          {currentUser ? 'Logout' : 'Login'}
        </button>
      </header>

      {error ? (
        <div className="error-toast">
          <span>{error}</span>
          <button type="button" onClick={() => setError('')}>×</button>
        </div>
      ) : null}

      {toast ? <div className={`floating-toast ${toast.type}`}>{toast.message}</div> : null}

      {showLoginPanel && !currentUser ? (
        <>
          <div className="modal-backdrop" onClick={() => setShowLoginPanel(false)} />
          <section className="login-modal">
            <h3>Sign in</h3>
            <form onSubmit={handleLoginSubmit}>
              <label>
                Email
                <input
                  list="known-users"
                  value={loginEmail}
                  onChange={(event) => setLoginEmail(event.target.value)}
                  placeholder="user@example.com"
                  required
                />
                <datalist id="known-users">
                  {users.map((user) => (
                    <option key={user.id} value={user.email} />
                  ))}
                </datalist>
              </label>
              <div className="modal-actions">
                <button type="button" className="close-btn" onClick={() => setShowLoginPanel(false)}>
                  Cancel
                </button>
                <button className="auth-btn" disabled={busy}>Sign in</button>
              </div>
              {authInfo ? <p className="auth-info">{authInfo}</p> : null}
            </form>
          </section>
        </>
      ) : null}

      {activePage === 'admin' ? (
        <main className="admin-page">
          {isAdminUser ? (
            <>
              <section className="admin-page-head">
                <h2>Admin workspace</h2>
                <p>Create, edit and delete roadmaps and steps from one page.</p>
              </section>

              <section className="admin-form admin-form-wide">
                <h4>Roadmap selection</h4>
                <label>
                  Roadmap
                  <select
                    value={selectedRoadmapId || ''}
                    onChange={handleAdminRoadmapSelect}
                  >
                    <option value="">Choose roadmap</option>
                    {roadmaps.map((roadmap) => (
                      <option key={roadmap.id} value={roadmap.id}>
                        {normalizeCatalogTitle(roadmap.title)}
                      </option>
                    ))}
                  </select>
                </label>
              </section>

              <section className="admin-grid">
                <form className="admin-form" onSubmit={handleAdminCreateRoadmap}>
                  <h4>Create roadmap</h4>
                  <label>
                    Title
                    <input
                      value={adminRoadmapTitle}
                      onChange={(event) => setAdminRoadmapTitle(event.target.value)}
                      placeholder="For example: Data Science"
                      required
                    />
                  </label>
                  <label>
                    Description
                    <textarea
                      value={adminRoadmapDescription}
                      onChange={(event) => setAdminRoadmapDescription(event.target.value)}
                      rows={3}
                      placeholder="Short direction description"
                    />
                  </label>
                  <button className="auth-btn" disabled={busy}>Create roadmap</button>
                </form>

                <form className="admin-form" onSubmit={handleAdminUpdateRoadmap}>
                  <h4>Edit selected roadmap</h4>
                  {!selectedRoadmap ? (
                    <p className="admin-note">Select roadmap above first.</p>
                  ) : (
                    <p className="admin-note">{normalizeCatalogTitle(selectedRoadmap.title)}</p>
                  )}
                  <label>
                    Title
                    <input
                      value={adminEditRoadmapTitle}
                      onChange={(event) => setAdminEditRoadmapTitle(event.target.value)}
                      placeholder="Roadmap title"
                      required
                      disabled={!selectedRoadmap}
                    />
                  </label>
                  <label>
                    Description
                    <textarea
                      value={adminEditRoadmapDescription}
                      onChange={(event) => setAdminEditRoadmapDescription(event.target.value)}
                      rows={3}
                      placeholder="Roadmap description"
                      disabled={!selectedRoadmap}
                    />
                  </label>
                  <div className="admin-form-actions">
                    <button className="auth-btn" disabled={busy || !selectedRoadmap}>Save roadmap</button>
                    <button
                      type="button"
                      className="close-btn danger-btn"
                      disabled={busy || !selectedRoadmap}
                      onClick={handleAdminDeleteRoadmap}
                    >
                      Delete roadmap
                    </button>
                  </div>
                </form>

                <form className="admin-form" onSubmit={handleAdminCreateStep}>
                  <h4>Create step</h4>
                  {!selectedRoadmap ? (
                    <p className="admin-note">Select roadmap above first.</p>
                  ) : (
                    <p className="admin-note">{normalizeCatalogTitle(selectedRoadmap.title)}</p>
                  )}
                  <label>
                    Step title
                    <input
                      value={adminStepTitle}
                      onChange={(event) => setAdminStepTitle(event.target.value)}
                      placeholder="For example: SQL Basics"
                      required
                      disabled={!selectedRoadmap}
                    />
                  </label>
                  <label>
                    Step description
                    <textarea
                      value={adminStepDetails}
                      onChange={(event) => setAdminStepDetails(event.target.value)}
                      rows={3}
                      placeholder="What to study in this step"
                      disabled={!selectedRoadmap}
                    />
                  </label>
                  <label>
                    Status
                    <select
                      value={adminStepStatus}
                      onChange={(event) => setAdminStepStatus(event.target.value)}
                      disabled={!selectedRoadmap}
                    >
                      {ITEM_STATUSES.map((status) => (
                        <option key={status} value={status}>
                          {formatStatus(status)}
                        </option>
                      ))}
                    </select>
                  </label>
                  <label>
                    Parent step
                    <select
                      value={adminStepParentId}
                      onChange={(event) => setAdminStepParentId(event.target.value)}
                      disabled={!selectedRoadmap}
                    >
                      <option value="">No parent</option>
                      {roadmapItems.map((item) => (
                        <option key={item.id} value={item.id}>
                          {item.title}
                        </option>
                      ))}
                    </select>
                  </label>
                  <label>
                    Tags
                    <select
                      multiple
                      value={adminStepTagIds.map(String)}
                      onChange={handleAdminTagSelect}
                      size={Math.min(8, Math.max(4, tags.length))}
                      disabled={!selectedRoadmap}
                    >
                      {tags.map((tag) => (
                        <option key={tag.id} value={tag.id}>
                          {tag.name}
                        </option>
                      ))}
                    </select>
                  </label>
                  <button className="auth-btn" disabled={busy || !selectedRoadmap}>
                    Add step
                  </button>
                </form>

                <form className="admin-form" onSubmit={handleAdminUpdateStep}>
                  <h4>Edit step</h4>
                  <label>
                    Step
                    <select
                      value={adminEditStepId}
                      onChange={(event) => setAdminEditStepId(event.target.value)}
                      disabled={!selectedRoadmap}
                    >
                      <option value="">Choose step</option>
                      {roadmapItems.map((item) => (
                        <option key={item.id} value={item.id}>
                          {item.title}
                        </option>
                      ))}
                    </select>
                  </label>
                  <label>
                    Step title
                    <input
                      value={adminEditStepTitle}
                      onChange={(event) => setAdminEditStepTitle(event.target.value)}
                      placeholder="Step title"
                      required
                      disabled={!adminEditableStep}
                    />
                  </label>
                  <label>
                    Step description
                    <textarea
                      value={adminEditStepDetails}
                      onChange={(event) => setAdminEditStepDetails(event.target.value)}
                      rows={3}
                      placeholder="Step description"
                      disabled={!adminEditableStep}
                    />
                  </label>
                  <label>
                    Status
                    <select
                      value={adminEditStepStatus}
                      onChange={(event) => setAdminEditStepStatus(event.target.value)}
                      disabled={!adminEditableStep}
                    >
                      {ITEM_STATUSES.map((status) => (
                        <option key={status} value={status}>
                          {formatStatus(status)}
                        </option>
                      ))}
                    </select>
                  </label>
                  <label>
                    Parent step
                    <select
                      value={adminEditStepParentId}
                      onChange={(event) => setAdminEditStepParentId(event.target.value)}
                      disabled={!adminEditableStep}
                    >
                      <option value="">No parent</option>
                      {roadmapItems
                        .filter((item) => String(item.id) !== String(adminEditStepId))
                        .map((item) => (
                          <option key={item.id} value={item.id}>
                            {item.title}
                          </option>
                        ))}
                    </select>
                  </label>
                  <label>
                    Tags
                    <select
                      multiple
                      value={adminEditStepTagIds.map(String)}
                      onChange={handleAdminEditStepTagSelect}
                      size={Math.min(8, Math.max(4, tags.length))}
                      disabled={!adminEditableStep}
                    >
                      {tags.map((tag) => (
                        <option key={tag.id} value={tag.id}>
                          {tag.name}
                        </option>
                      ))}
                    </select>
                  </label>
                  <div className="admin-form-actions">
                    <button className="auth-btn" disabled={busy || !adminEditableStep}>
                      Save step
                    </button>
                    <button
                      type="button"
                      className="close-btn danger-btn"
                      disabled={busy || !adminEditableStep}
                      onClick={handleAdminDeleteStep}
                    >
                      Delete step
                    </button>
                  </div>
                </form>

                <form className="admin-form admin-form-wide" onSubmit={handleAdminCreateBulkSteps}>
                  <h4>Bulk step creation</h4>
                  <p className="admin-note">One step per line. Format: `Title | description`.</p>
                  <label>
                    Step list
                    <textarea
                      value={adminBulkStepsText}
                      onChange={(event) => setAdminBulkStepsText(event.target.value)}
                      rows={8}
                      placeholder={'Python Basics | syntax and types\nSQL Fundamentals | select, join, group by\nAlgorithms'}
                      required
                      disabled={!selectedRoadmap}
                    />
                  </label>
                  <label>
                    Status for all
                    <select
                      value={adminBulkStatus}
                      onChange={(event) => setAdminBulkStatus(event.target.value)}
                      disabled={!selectedRoadmap}
                    >
                      {ITEM_STATUSES.map((status) => (
                        <option key={status} value={status}>
                          {formatStatus(status)}
                        </option>
                      ))}
                    </select>
                  </label>
                  <label>
                    Starting parent
                    <select
                      value={adminBulkParentId}
                      onChange={(event) => setAdminBulkParentId(event.target.value)}
                      disabled={!selectedRoadmap}
                    >
                      <option value="">No parent</option>
                      {roadmapItems.map((item) => (
                        <option key={item.id} value={item.id}>
                          {item.title}
                        </option>
                      ))}
                    </select>
                  </label>
                  <label className="inline-check">
                    <input
                      type="checkbox"
                      checked={adminBulkChain}
                      onChange={(event) => setAdminBulkChain(event.target.checked)}
                      disabled={!selectedRoadmap}
                    />
                    Link as sequential chain
                  </label>
                  <label>
                    Tags for all
                    <select
                      multiple
                      value={adminBulkTagIds.map(String)}
                      onChange={handleAdminBulkTagSelect}
                      size={Math.min(8, Math.max(4, tags.length))}
                      disabled={!selectedRoadmap}
                    >
                      {tags.map((tag) => (
                        <option key={tag.id} value={tag.id}>
                          {tag.name}
                        </option>
                      ))}
                    </select>
                  </label>
                  <button className="auth-btn" disabled={busy || !selectedRoadmap}>
                    Add all steps
                  </button>
                </form>
              </section>
            </>
          ) : (
            <div className="empty-box">Admin access required.</div>
          )}
        </main>
      ) : (
        <>
          <div className={`drawer-backdrop ${sidebarOpen ? 'show' : ''}`} onClick={() => setSidebarOpen(false)} />

          <aside className={`roadmap-drawer ${sidebarOpen ? 'open' : ''}`}>
            <div className="drawer-head">
              <h2>Roadmaps</h2>
              <button className="close-btn" onClick={() => setSidebarOpen(false)}>
                Close
              </button>
            </div>
            <div className="drawer-list">
              {searchableRoadmaps.map((roadmap) => (
                <button
                  key={roadmap.id}
                  className={`drawer-item ${selectedRoadmapId === roadmap.id ? 'active' : ''}`}
                  onClick={() => selectRoadmap(roadmap.id)}
                >
                  <strong>{normalizeCatalogTitle(roadmap.title)}</strong>
                  <span>{roadmap.description || 'No description'}</span>
                </button>
              ))}
            </div>
          </aside>

          <main className={`main-shell ${selectedItem ? 'with-details' : 'single'}`}>
            <section className="graph-panel">
              <div className="roadmap-head">
                <h2>{selectedRoadmap ? normalizeCatalogTitle(selectedRoadmap.title) : 'Select a roadmap'}</h2>
                {selectedRoadmap ? <p>{selectedRoadmap.description || ''}</p> : null}
              </div>

              <div className="graph-stage">
                {loadingItems ? <div className="overlay">Loading...</div> : null}

                {!selectedRoadmap ? (
                  <div className="center-roadmaps">
                    {searchableRoadmaps.map((roadmap) => (
                      <button
                        key={roadmap.id}
                        type="button"
                        className="center-roadmap-card"
                        onClick={() => selectRoadmap(roadmap.id)}
                      >
                        <strong>{normalizeCatalogTitle(roadmap.title)}</strong>
                        <span>{roadmap.description || 'No description'}</span>
                      </button>
                    ))}
                    {searchableRoadmaps.length === 0 ? (
                      <div className="empty-box">No roadmaps available yet.</div>
                    ) : null}
                  </div>
                ) : filteredItems.length === 0 ? (
                  <div className="empty-box">No steps in this roadmap yet.</div>
                ) : (
                  <div className="graph-canvas" style={{ width: graph.width, height: graph.height }}>
                    <svg className="graph-svg" viewBox={`0 0 ${graph.width} ${graph.height}`}>
                      {graph.edges.map((edge) => (
                        <path key={edge.id} d={edge.path} className="graph-edge" />
                      ))}
                    </svg>

                    {graph.nodes.map(({ item, x, y, width, height }) => (
                      <button
                        type="button"
                        key={item.id}
                        className={`graph-node ${statusClass(item.status)} ${selectedItemId === item.id ? 'selected' : ''}`}
                        style={{ left: x, top: y, width, height }}
                        onClick={() => setSelectedItemId(item.id)}
                      >
                        <small>{formatStatus(item.status)}</small>
                        <strong>{item.title}</strong>
                      </button>
                    ))}
                  </div>
                )}
              </div>
            </section>

            {selectedItem ? (
              <aside className="details-panel">
                <h3>Step details</h3>
                <div className={`details-card ${statusClass(selectedItem.status)}`}>
                  <p className="status-pill">{formatStatus(selectedItem.status)}</p>
                  <h4>{selectedItem.title}</h4>
                  <p>{selectedItem.details || 'No details'}</p>
                </div>

                <section className="progress-editor">
                  <h4>Progress status</h4>
                  <label>
                    Status
                    <select
                      value={statusDraft}
                      onChange={(event) => handleStatusDraftChange(event.target.value)}
                    >
                      {getAllowedStatuses(selectedItem.status || 'PLANNED').map((status) => (
                        <option key={status} value={status}>
                          {formatStatus(status)}
                        </option>
                      ))}
                    </select>
                  </label>
                  <button className="auth-btn" onClick={saveStatusForSelectedItem} disabled={busy}>
                    Save status
                  </button>
                </section>

                <section className="links-preview">
                  <h4>Learning links</h4>
                  <div className="links-list">
                    {stepLearningLinks.map((resource) => (
                      <a key={resource.url} href={resource.url} target="_blank" rel="noreferrer">
                        {resource.title}
                      </a>
                    ))}
                    {stepLearningLinks.length === 0 ? <span className="chip">Links unavailable</span> : null}
                  </div>
                </section>

                <section className="tags-preview">
                  <h4>Tags</h4>
                  <div className="chip-list">
                    {selectedItem.tagIds.map((tagId) => (
                      <span key={tagId} className="chip">
                        {tagsById.get(tagId)?.name || `tag#${tagId}`}
                      </span>
                    ))}
                    {selectedItem.tagIds.length === 0 ? <span className="chip">No tags</span> : null}
                  </div>
                </section>
              </aside>
            ) : null}
          </main>
        </>
      )}
    </div>
  );
}
