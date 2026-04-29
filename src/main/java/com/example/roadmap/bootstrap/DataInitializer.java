package com.example.roadmap.bootstrap;

import com.example.roadmap.model.Comment;
import com.example.roadmap.model.ItemStatus;
import com.example.roadmap.model.RoadMap;
import com.example.roadmap.model.RoadMapItem;
import com.example.roadmap.model.Tag;
import com.example.roadmap.model.User;
import com.example.roadmap.repository.CommentRepository;
import com.example.roadmap.repository.RoadMapItemRepository;
import com.example.roadmap.repository.RoadMapRepository;
import com.example.roadmap.repository.TagRepository;
import com.example.roadmap.repository.UserRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!test")
@RequiredArgsConstructor
@Transactional
public class DataInitializer implements CommandLineRunner {

  private static final String DEMO_OWNER_EMAIL = "vladislav@example.com";
  private static final String CATALOG_PREFIX = "Roadmap: ";

  private final UserRepository userRepository;
  private final TagRepository tagRepository;
  private final RoadMapRepository roadMapRepository;
  private final RoadMapItemRepository roadMapItemRepository;
  private final CommentRepository commentRepository;

  @Override
  public void run(String... args) {
    User demoOwner = ensureDemoOwner();
    Map<String, Tag> tagsByName = ensureTags();

    seedJavaBackendRoadmap(demoOwner, tagsByName);
    seedCatalogRoadmaps(demoOwner, tagsByName);
  }

  private User ensureDemoOwner() {
    return userRepository.findByEmailIgnoreCase(DEMO_OWNER_EMAIL)
        .orElseGet(() -> {
          User user = new User();
          user.setFirstName("Vladislav");
          user.setLastName("Mogilny");
          user.setEmail(DEMO_OWNER_EMAIL);
          return userRepository.save(user);
        });
  }

  private Map<String, Tag> ensureTags() {
    List<String> names = List.of(
        "spring", "sql", "java", "python", "math", "machine-learning",
        "deep-learning", "frontend", "javascript", "react", "devops",
        "docker", "kubernetes", "security", "cloud", "linux", "git",
        "data-engineering", "etl", "kafka", "spark", "airflow", "nosql",
        "microservices", "testing", "android", "ios", "swift", "kotlin", "mobile",
        "networking", "cryptography", "system-design", "architecture",
        "product", "analytics", "observability", "ci-cd", "mlops", "nlp", "cv");

    Map<String, Tag> result = new LinkedHashMap<>();
    for (String name : names) {
      Tag tag = tagRepository.findByNameIgnoreCase(name)
          .orElseGet(() -> {
            Tag entity = new Tag();
            entity.setName(name);
            return tagRepository.save(entity);
          });
      result.put(normalizeKey(name), tag);
    }
    return result;
  }

  private void seedJavaBackendRoadmap(User owner, Map<String, Tag> tagsByName) {
    RoadMap roadmap = ensureRoadMap(
        owner,
        "Java Backend Roadmap",
        "Preparation roadmap for semester labs");

    RoadMapItem jpa = ensureRoadMapItem(
        roadmap,
        "Learn JPA basics",
        "Entity mapping, repositories, relationships",
        ItemStatus.IN_PROGRESS,
        null,
        tagsByName,
        List.of("java", "spring", "sql"));

    RoadMapItem transactions = ensureRoadMapItem(
        roadmap,
        "Master transactions",
        "Understand transactional boundaries and rollback behavior",
        ItemStatus.PLANNED,
        "Learn JPA basics",
        tagsByName,
        List.of("spring", "sql"));

    ensureRoadMapItem(
        roadmap,
        "Handle async background tasks",
        "Implement task polling and status tracking",
        ItemStatus.PLANNED,
        "Master transactions",
        tagsByName,
        List.of("spring", "java"));

    ensureCommentIfMissing(
        jpa,
        owner,
        "Need extra practice with ManyToMany.");
    ensureCommentIfMissing(
        transactions,
        owner,
        "Add retry logic after lab demonstration.");
  }

  private void seedCatalogRoadmaps(User owner, Map<String, Tag> tagsByName) {
    List<RoadmapSeed> seeds = List.of(
        machineLearningSeed(),
        dataEngineeringSeed(),
        backendEngineeringSeed(),
        frontendEngineeringSeed(),
        devOpsSeed(),
        cybersecuritySeed(),
        mobileSeed(),
        qaSeed(),
        systemDesignSeed(),
        productAnalyticsSeed()
    );

    for (RoadmapSeed seed : seeds) {
      RoadMap roadmap = ensureRoadMap(owner, seed.title(), seed.description());
      RoadMapItem firstItem = null;
      for (ItemSeed itemSeed : seed.items()) {
        RoadMapItem saved = ensureRoadMapItem(
            roadmap,
            itemSeed.title(),
            itemSeed.details(),
            itemSeed.status(),
            itemSeed.parentTitle(),
            tagsByName,
            itemSeed.tagNames());
        if (firstItem == null) {
          firstItem = saved;
        }
      }
      if (firstItem != null) {
        ensureCommentIfMissing(
            firstItem,
            owner,
            "Catalog roadmap is ready for UI visualization.");
      }
    }
  }

  private RoadmapSeed machineLearningSeed() {
    return new RoadmapSeed(
        CATALOG_PREFIX + "Machine Learning",
        "Large roadmap: from foundations to deep learning and production ML systems.",
        buildLinearItems(List.of(
            new StepTemplate("Orientation and Learning Plan",
                "Set goals, define timeline, and pick tooling.",
                List.of("machine-learning", "product")),
            new StepTemplate("Python Foundations",
                "Core syntax, data structures, functions, and OOP.",
                List.of("python")),
            new StepTemplate("Git, CLI, and Environment Setup",
                "Version control and reproducible development workflow.",
                List.of("git", "linux")),
            new StepTemplate("NumPy and Vectorized Computing",
                "Arrays, broadcasting, and numerical operations.",
                List.of("python", "math")),
            new StepTemplate("Pandas for Data Analysis",
                "Data cleaning, joins, and table transformations.",
                List.of("python", "analytics")),
            new StepTemplate("SQL for Analytical Workloads",
                "Filtering, aggregations, window functions.",
                List.of("sql", "analytics")),
            new StepTemplate("Data Visualization",
                "Plotting distributions and relationships.",
                List.of("python", "analytics")),
            new StepTemplate("Linear Algebra Essentials",
                "Vectors, matrices, rank, decompositions.",
                List.of("math", "machine-learning")),
            new StepTemplate("Calculus for Optimization",
                "Derivatives, gradients, and chain rule intuition.",
                List.of("math", "machine-learning")),
            new StepTemplate("Probability and Statistics",
                "Random variables, distributions, confidence intervals.",
                List.of("math", "machine-learning")),
            new StepTemplate("Feature Engineering Basics",
                "Encoding, scaling, and robust feature design.",
                List.of("machine-learning", "analytics")),
            new StepTemplate("Train/Validation/Test Strategy",
                "Avoiding leakage and overfitting in experiments.",
                List.of("machine-learning", "testing")),
            new StepTemplate("Linear Regression",
                "Baseline regression model and diagnostics.",
                List.of("machine-learning")),
            new StepTemplate("Logistic Regression",
                "Binary classification and probability outputs.",
                List.of("machine-learning")),
            new StepTemplate("Model Metrics and Thresholds",
                "Accuracy, precision/recall, ROC-AUC.",
                List.of("machine-learning", "analytics")),
            new StepTemplate("Decision Trees",
                "Interpretable tree-based models.",
                List.of("machine-learning")),
            new StepTemplate("Random Forest and Bagging",
                "Variance reduction via ensembles.",
                List.of("machine-learning")),
            new StepTemplate("Gradient Boosting",
                "XGBoost/LightGBM/CatBoost concepts.",
                List.of("machine-learning")),
            new StepTemplate("Hyperparameter Optimization",
                "Grid/random search and Bayesian approaches.",
                List.of("machine-learning", "testing")),
            new StepTemplate("Unsupervised Learning",
                "Clustering and dimensionality reduction.",
                List.of("machine-learning")),
            new StepTemplate("Pipelines and Reproducibility",
                "Feature + model pipelines with stable experiments.",
                List.of("machine-learning", "mlops")),
            new StepTemplate("Introduction to Neural Networks",
                "Perceptron, activations, feed-forward networks.",
                List.of("deep-learning", "machine-learning")),
            new StepTemplate("Backpropagation and Optimization",
                "Gradients, SGD, Adam, learning rates.",
                List.of("deep-learning", "math")),
            new StepTemplate("PyTorch Fundamentals",
                "Tensors, autograd, and training loops.",
                List.of("deep-learning", "python")),
            new StepTemplate("Regularization Techniques",
                "Dropout, weight decay, early stopping.",
                List.of("deep-learning", "testing")),
            new StepTemplate("Computer Vision Fundamentals",
                "Image preprocessing and convolution basics.",
                List.of("cv", "deep-learning")),
            new StepTemplate("CNN Architectures",
                "ResNet, transfer learning, fine-tuning.",
                List.of("cv", "deep-learning")),
            new StepTemplate("Natural Language Processing Basics",
                "Tokenization, embeddings, sequence models.",
                List.of("nlp", "deep-learning")),
            new StepTemplate("Transformer Architecture",
                "Attention mechanism and modern NLP models.",
                List.of("nlp", "deep-learning")),
            new StepTemplate("Time Series Forecasting",
                "Trend/seasonality models and backtesting.",
                List.of("machine-learning", "analytics")),
            new StepTemplate("Experiment Tracking",
                "Track metrics, params, and artifacts.",
                List.of("mlops", "observability")),
            new StepTemplate("Data Versioning",
                "Version datasets and feature snapshots.",
                List.of("data-engineering", "mlops")),
            new StepTemplate("Model Packaging",
                "Containerize model with reproducible runtime.",
                List.of("docker", "mlops")),
            new StepTemplate("Serving Inference API",
                "Expose prediction endpoint and contracts.",
                List.of("mlops", "microservices")),
            new StepTemplate("Batch and Streaming Inference",
                "Design online/offline prediction paths.",
                List.of("mlops", "kafka")),
            new StepTemplate("Model Monitoring",
                "Track drift, latency, and prediction quality.",
                List.of("observability", "mlops")),
            new StepTemplate("Feature Store Concepts",
                "Consistency between training and serving features.",
                List.of("data-engineering", "mlops")),
            new StepTemplate("CI/CD for ML",
                "Automate tests, model build and deployment.",
                List.of("ci-cd", "mlops")),
            new StepTemplate("Kubernetes Deployment",
                "Deploy scalable model services.",
                List.of("kubernetes", "cloud")),
            new StepTemplate("Responsible AI",
                "Bias, fairness, explainability and governance.",
                List.of("machine-learning", "product")),
            new StepTemplate("A/B Testing for ML Products",
                "Online evaluation and rollout strategy.",
                List.of("analytics", "product")),
            new StepTemplate("Production Incident Playbook",
                "Diagnose model failures and rollback fast.",
                List.of("observability", "devops")),
            new StepTemplate("Capstone: End-to-End ML System",
                "From data ingestion to monitored production model.",
                List.of("machine-learning", "mlops", "cloud"))
        ))
    );
  }

  private RoadmapSeed dataEngineeringSeed() {
    return new RoadmapSeed(
        CATALOG_PREFIX + "Data Engineering",
        "Building reliable pipelines, warehouses, and streaming platforms.",
        buildLinearItems(List.of(
            new StepTemplate("Linux and Shell Basics",
                "Filesystem, process control, shell utilities.",
                List.of("linux")),
            new StepTemplate("Python for ETL",
                "File processing, API ingestion, automation scripts.",
                List.of("python", "etl")),
            new StepTemplate("SQL Advanced Patterns",
                "Window functions, CTEs, query optimization.",
                List.of("sql", "analytics")),
            new StepTemplate("Data Modeling Fundamentals",
                "Normalization, star schema, dimensions/facts.",
                List.of("data-engineering", "analytics")),
            new StepTemplate("Batch Pipeline Design",
                "DAG-oriented ETL and idempotent jobs.",
                List.of("etl", "data-engineering")),
            new StepTemplate("Airflow Orchestration",
                "Schedules, dependencies, retries, monitoring.",
                List.of("airflow", "etl")),
            new StepTemplate("Distributed Processing with Spark",
                "Partitioning, joins, and performance tuning.",
                List.of("spark", "data-engineering")),
            new StepTemplate("Data Lake Fundamentals",
                "Object storage, partitioning, and file formats.",
                List.of("cloud", "data-engineering")),
            new StepTemplate("Warehouse Fundamentals",
                "Columnar storage and BI workloads.",
                List.of("data-engineering", "analytics")),
            new StepTemplate("Streaming Concepts",
                "Event-driven architecture and consumer groups.",
                List.of("kafka", "data-engineering")),
            new StepTemplate("Kafka in Practice",
                "Topics, retention, schema evolution.",
                List.of("kafka", "data-engineering")),
            new StepTemplate("Change Data Capture",
                "Capture database updates to event streams.",
                List.of("kafka", "sql")),
            new StepTemplate("Data Quality Checks",
                "Schema tests, null checks, anomaly detection.",
                List.of("testing", "data-engineering")),
            new StepTemplate("Observability for Pipelines",
                "Logging, metrics, alerting for SLA.",
                List.of("observability", "devops")),
            new StepTemplate("Containerized Jobs",
                "Run ETL in Docker images.",
                List.of("docker", "etl")),
            new StepTemplate("Infrastructure as Code",
                "Provision cloud resources declaratively.",
                List.of("cloud", "devops")),
            new StepTemplate("Data Security and Governance",
                "Access control, lineage, retention policies.",
                List.of("security", "data-engineering")),
            new StepTemplate("Cost Optimization",
                "Control compute/storage and optimize queries.",
                List.of("cloud", "analytics")),
            new StepTemplate("CI/CD for Pipelines",
                "Automate deploys and migration checks.",
                List.of("ci-cd", "data-engineering")),
            new StepTemplate("Capstone: Streaming + Batch Platform",
                "Unified data platform with quality and monitoring.",
                List.of("data-engineering", "kafka", "airflow"))
        ))
    );
  }

  private RoadmapSeed backendEngineeringSeed() {
    return new RoadmapSeed(
        CATALOG_PREFIX + "Backend Engineering",
        "Design and build scalable, testable backend systems.",
        buildLinearItems(List.of(
            new StepTemplate("Java Core and OOP",
                "Language fundamentals and clean abstractions.",
                List.of("java")),
            new StepTemplate("Collections and Concurrency",
                "Threading, executors, synchronization.",
                List.of("java")),
            new StepTemplate("Spring Boot Basics",
                "Dependency injection and application setup.",
                List.of("spring", "java")),
            new StepTemplate("REST API Design",
                "Resource modeling and HTTP semantics.",
                List.of("microservices", "java")),
            new StepTemplate("Validation and Error Handling",
                "Robust contracts and predictable responses.",
                List.of("spring", "testing")),
            new StepTemplate("SQL and Indexing",
                "Schema design and query performance.",
                List.of("sql", "java")),
            new StepTemplate("JPA and Transactions",
                "Entity mapping and consistency boundaries.",
                List.of("spring", "sql")),
            new StepTemplate("Caching Strategies",
                "Reduce latency and pressure on DB.",
                List.of("microservices", "java")),
            new StepTemplate("Messaging and Async Processing",
                "Background tasks and eventual consistency.",
                List.of("kafka", "microservices")),
            new StepTemplate("Authentication and Authorization",
                "Token-based auth and role model.",
                List.of("security", "spring")),
            new StepTemplate("Testing Pyramid",
                "Unit, integration, contract testing.",
                List.of("testing", "java")),
            new StepTemplate("API Documentation",
                "OpenAPI and consumer-friendly docs.",
                List.of("spring", "product")),
            new StepTemplate("Docker for Backend",
                "Package and run services in containers.",
                List.of("docker", "devops")),
            new StepTemplate("Observability",
                "Structured logs, metrics, tracing.",
                List.of("observability", "devops")),
            new StepTemplate("System Reliability",
                "Timeouts, retries, circuit breaker patterns.",
                List.of("architecture", "microservices")),
            new StepTemplate("Horizontal Scaling",
                "Stateless services and load balancing.",
                List.of("cloud", "architecture")),
            new StepTemplate("CI/CD",
                "Automated pipelines and quality gates.",
                List.of("ci-cd", "devops")),
            new StepTemplate("Capstone: Production Backend Service",
                "Secure, monitored, deployable backend application.",
                List.of("java", "spring", "cloud"))
        ))
    );
  }

  private RoadmapSeed frontendEngineeringSeed() {
    return new RoadmapSeed(
        CATALOG_PREFIX + "Frontend Engineering",
        "From browser basics to scalable frontend architecture.",
        buildLinearItems(List.of(
            new StepTemplate("HTML and Semantic Layout",
                "Build accessible, meaningful structure.",
                List.of("frontend")),
            new StepTemplate("CSS Foundations",
                "Layout systems, responsive UI, component styles.",
                List.of("frontend")),
            new StepTemplate("JavaScript Core",
                "Language fundamentals, async, modules.",
                List.of("javascript")),
            new StepTemplate("TypeScript Basics",
                "Safer APIs and typed components.",
                List.of("javascript", "frontend")),
            new StepTemplate("React Fundamentals",
                "State, props, hooks and composition.",
                List.of("react", "frontend")),
            new StepTemplate("Routing and Navigation",
                "Client-side route architecture.",
                List.of("react", "frontend")),
            new StepTemplate("State Management",
                "Server/client state patterns.",
                List.of("react", "javascript")),
            new StepTemplate("Forms and Validation",
                "Reliable input handling and UX.",
                List.of("frontend", "testing")),
            new StepTemplate("Performance Optimization",
                "Memoization, splitting, and rendering strategy.",
                List.of("frontend", "react")),
            new StepTemplate("Testing Frontend",
                "Unit, integration and e2e tests.",
                List.of("testing", "frontend")),
            new StepTemplate("Design System",
                "Reusable components and tokens.",
                List.of("frontend", "product")),
            new StepTemplate("Accessibility",
                "Keyboard navigation and semantic interactions.",
                List.of("frontend", "product")),
            new StepTemplate("Frontend Security",
                "XSS, CSP and safe data rendering.",
                List.of("security", "frontend")),
            new StepTemplate("Monitoring and RUM",
                "Track errors and UX performance in production.",
                List.of("observability", "frontend")),
            new StepTemplate("Build and Bundling",
                "Vite/Webpack optimization and delivery.",
                List.of("frontend", "devops")),
            new StepTemplate("Capstone: Production SPA",
                "Large frontend with robust architecture and testing.",
                List.of("react", "frontend", "ci-cd"))
        ))
    );
  }

  private RoadmapSeed devOpsSeed() {
    return new RoadmapSeed(
        CATALOG_PREFIX + "DevOps Platform",
        "Automate delivery and run systems with high reliability.",
        buildLinearItems(List.of(
            new StepTemplate("Linux Administration",
                "Users, permissions, networking, process diagnostics.",
                List.of("linux", "devops")),
            new StepTemplate("Networking Fundamentals",
                "TCP/IP, DNS, HTTP, TLS basics.",
                List.of("networking", "devops")),
            new StepTemplate("Bash and Automation",
                "Scripting repetitive ops tasks.",
                List.of("linux", "devops")),
            new StepTemplate("Git Workflows",
                "Branch strategy and release flow.",
                List.of("git", "devops")),
            new StepTemplate("Docker",
                "Images, layers, runtime and registries.",
                List.of("docker", "devops")),
            new StepTemplate("Kubernetes Basics",
                "Pods, deployments, services, namespaces.",
                List.of("kubernetes", "devops")),
            new StepTemplate("Kubernetes Operations",
                "Autoscaling, config, secrets, rollout strategies.",
                List.of("kubernetes", "devops")),
            new StepTemplate("Ingress and API Gateways",
                "Traffic management and service exposure.",
                List.of("kubernetes", "networking")),
            new StepTemplate("CI/CD Pipelines",
                "Build, test, deploy automation.",
                List.of("ci-cd", "devops")),
            new StepTemplate("Infrastructure as Code",
                "Versioned infrastructure provisioning.",
                List.of("cloud", "devops")),
            new StepTemplate("Cloud Fundamentals",
                "Compute, storage, networking primitives.",
                List.of("cloud", "devops")),
            new StepTemplate("Observability Stack",
                "Metrics, logs, tracing and alerting.",
                List.of("observability", "devops")),
            new StepTemplate("SRE Practices",
                "SLO, error budgets, postmortems.",
                List.of("observability", "architecture")),
            new StepTemplate("Security Hardening",
                "Least privilege, secrets, vulnerability scanning.",
                List.of("security", "devops")),
            new StepTemplate("Backup and Disaster Recovery",
                "Recovery strategy and chaos drills.",
                List.of("cloud", "security")),
            new StepTemplate("Cost and Capacity Management",
                "Rightsizing and scaling economics.",
                List.of("cloud", "analytics")),
            new StepTemplate("Incident Response",
                "Runbooks, escalation and communication.",
                List.of("observability", "devops")),
            new StepTemplate("Capstone: Production DevOps Stack",
                "Automated, secure, observable deployment platform.",
                List.of("devops", "kubernetes", "ci-cd"))
        ))
    );
  }

  private RoadmapSeed cybersecuritySeed() {
    return new RoadmapSeed(
        CATALOG_PREFIX + "Cybersecurity",
        "Security fundamentals, threat modeling, and defensive operations.",
        buildLinearItems(List.of(
            new StepTemplate("Security Fundamentals",
                "CIA triad and common attack vectors.",
                List.of("security")),
            new StepTemplate("Networking for Security",
                "Protocols, packet flow, segmentation.",
                List.of("networking", "security")),
            new StepTemplate("Operating System Security",
                "Hardening Linux and Windows baselines.",
                List.of("linux", "security")),
            new StepTemplate("Identity and Access Management",
                "Authentication, authorization, MFA.",
                List.of("security", "cloud")),
            new StepTemplate("Cryptography Basics",
                "Hashing, symmetric/asymmetric encryption.",
                List.of("cryptography", "security")),
            new StepTemplate("Secure Development Lifecycle",
                "Shift-left security in delivery pipeline.",
                List.of("security", "ci-cd")),
            new StepTemplate("Web Security",
                "OWASP Top 10 and mitigation practices.",
                List.of("security", "frontend")),
            new StepTemplate("API Security",
                "Auth flows, rate limiting, abuse prevention.",
                List.of("security", "microservices")),
            new StepTemplate("Cloud Security",
                "Cloud posture management and guardrails.",
                List.of("cloud", "security")),
            new StepTemplate("Container Security",
                "Image scanning and runtime policies.",
                List.of("docker", "kubernetes", "security")),
            new StepTemplate("SIEM and Log Analysis",
                "Correlate events and detect anomalies.",
                List.of("observability", "security")),
            new StepTemplate("Threat Modeling",
                "Identify assets, threats, and controls.",
                List.of("security", "architecture")),
            new StepTemplate("Vulnerability Management",
                "Scan, prioritize, and remediate findings.",
                List.of("security", "devops")),
            new StepTemplate("Incident Response",
                "Detection, containment, eradication, recovery.",
                List.of("security", "devops")),
            new StepTemplate("Penetration Testing Basics",
                "Ethical testing methodology and reporting.",
                List.of("security", "testing")),
            new StepTemplate("Capstone: Security Program",
                "Complete controls map with monitoring and response.",
                List.of("security", "observability"))
        ))
    );
  }

  private RoadmapSeed mobileSeed() {
    return new RoadmapSeed(
        CATALOG_PREFIX + "Mobile Engineering",
        "Native and cross-platform mobile development journey.",
        buildLinearItems(List.of(
            new StepTemplate("Mobile Architecture Basics",
                "UI layers, state, and navigation concepts.",
                List.of("product", "frontend")),
            new StepTemplate("Kotlin Essentials",
                "Language basics for Android development.",
                List.of("kotlin", "android")),
            new StepTemplate("Android UI and Lifecycle",
                "Activities, fragments, lifecycle handling.",
                List.of("android", "frontend")),
            new StepTemplate("Swift Essentials",
                "Language basics for iOS development.",
                List.of("swift", "ios")),
            new StepTemplate("iOS UI and Lifecycle",
                "View controllers and lifecycle events.",
                List.of("ios", "frontend")),
            new StepTemplate("API Integration",
                "Networking, retries, and offline strategies.",
                List.of("mobile", "microservices")),
            new StepTemplate("Persistence on Device",
                "Local storage and synchronization patterns.",
                List.of("mobile", "nosql")),
            new StepTemplate("Authentication on Mobile",
                "Secure token handling and biometric auth.",
                List.of("security", "mobile")),
            new StepTemplate("Performance and Battery",
                "Rendering and background optimization.",
                List.of("mobile", "analytics")),
            new StepTemplate("Push Notifications",
                "Reliable delivery and user engagement.",
                List.of("mobile", "cloud")),
            new StepTemplate("Mobile Testing",
                "Unit, UI, and device testing matrix.",
                List.of("testing", "mobile")),
            new StepTemplate("App Store Delivery",
                "Signing, release channels, rollout safety.",
                List.of("ci-cd", "mobile")),
            new StepTemplate("Crash Monitoring",
                "Production diagnostics and alerting.",
                List.of("observability", "mobile")),
            new StepTemplate("Capstone: Production Mobile App",
                "Release-ready app with monitoring and CI/CD.",
                List.of("android", "ios", "ci-cd"))
        ))
    );
  }

  private RoadmapSeed qaSeed() {
    return new RoadmapSeed(
        CATALOG_PREFIX + "QA Automation",
        "Testing strategy from manual checks to full automation pipelines.",
        buildLinearItems(List.of(
            new StepTemplate("Testing Fundamentals",
                "Levels of testing and defect lifecycle.",
                List.of("testing")),
            new StepTemplate("Test Case Design",
                "Boundary, equivalence, risk-based coverage.",
                List.of("testing", "product")),
            new StepTemplate("API Testing Basics",
                "Request/response validation and contracts.",
                List.of("testing", "microservices")),
            new StepTemplate("UI Testing Basics",
                "Page objects and stable selectors.",
                List.of("testing", "frontend")),
            new StepTemplate("Java for Test Automation",
                "Core language for automation framework.",
                List.of("java", "testing")),
            new StepTemplate("Selenium and WebDriver",
                "Browser automation and synchronization.",
                List.of("testing", "frontend")),
            new StepTemplate("Playwright",
                "Modern end-to-end test implementation.",
                List.of("testing", "javascript")),
            new StepTemplate("Contract Testing",
                "Service contracts between teams.",
                List.of("testing", "microservices")),
            new StepTemplate("Performance Testing",
                "Load, stress and bottleneck analysis.",
                List.of("testing", "observability")),
            new StepTemplate("Security Testing Basics",
                "Automated checks for common vulnerabilities.",
                List.of("security", "testing")),
            new StepTemplate("Test Data Management",
                "Reliable environments and fixtures.",
                List.of("testing", "data-engineering")),
            new StepTemplate("CI Integration for Tests",
                "Run suites in pipeline with quality gates.",
                List.of("ci-cd", "testing")),
            new StepTemplate("Flaky Test Reduction",
                "Stabilize tests and improve trust.",
                List.of("testing", "devops")),
            new StepTemplate("Capstone: Full QA Pipeline",
                "Automated quality process from API to UI.",
                List.of("testing", "ci-cd"))
        ))
    );
  }

  private RoadmapSeed systemDesignSeed() {
    return new RoadmapSeed(
        CATALOG_PREFIX + "System Design",
        "Design scalable distributed systems and platform architecture.",
        buildLinearItems(List.of(
            new StepTemplate("Requirements and Constraints",
                "Functional/non-functional analysis.",
                List.of("system-design", "architecture")),
            new StepTemplate("Capacity Estimation",
                "Traffic, storage, and throughput calculations.",
                List.of("system-design", "analytics")),
            new StepTemplate("Data Modeling for Scale",
                "Consistency trade-offs and access patterns.",
                List.of("system-design", "data-engineering")),
            new StepTemplate("Caching Layers",
                "Cache-aside and invalidation strategies.",
                List.of("system-design", "microservices")),
            new StepTemplate("Load Balancing",
                "L4/L7 balancing and failover options.",
                List.of("system-design", "networking")),
            new StepTemplate("Message Queues and Streams",
                "Async decoupling and event-driven design.",
                List.of("kafka", "system-design")),
            new StepTemplate("Database Sharding",
                "Horizontal scaling and key strategy.",
                List.of("sql", "system-design")),
            new StepTemplate("NoSQL Patterns",
                "Document/key-value models for scale.",
                List.of("nosql", "system-design")),
            new StepTemplate("Observability Architecture",
                "Distributed tracing and telemetry.",
                List.of("observability", "architecture")),
            new StepTemplate("Reliability and Resilience",
                "Retries, timeouts, backpressure.",
                List.of("architecture", "devops")),
            new StepTemplate("Security by Design",
                "Threat boundaries and defense in depth.",
                List.of("security", "architecture")),
            new StepTemplate("Global Deployment",
                "Multi-region, latency and DR planning.",
                List.of("cloud", "system-design")),
            new StepTemplate("Design Review Practice",
                "Communicate trade-offs and alternatives.",
                List.of("system-design", "product")),
            new StepTemplate("Capstone: Large-Scale Service Design",
                "Complete architecture for a high-load product.",
                List.of("system-design", "architecture"))
        ))
    );
  }

  private RoadmapSeed productAnalyticsSeed() {
    return new RoadmapSeed(
        CATALOG_PREFIX + "Product Analytics",
        "Measure product usage, growth and experimentation impact.",
        buildLinearItems(List.of(
            new StepTemplate("Analytics Foundations",
                "KPIs, funnels and cohort basics.",
                List.of("analytics", "product")),
            new StepTemplate("Event Taxonomy",
                "Consistent event naming and properties.",
                List.of("analytics", "product")),
            new StepTemplate("SQL for Product Metrics",
                "Build retention and conversion queries.",
                List.of("sql", "analytics")),
            new StepTemplate("Dashboard Design",
                "Actionable visuals and monitoring boards.",
                List.of("analytics", "product")),
            new StepTemplate("A/B Testing Fundamentals",
                "Experiment design and significance.",
                List.of("analytics", "product")),
            new StepTemplate("Attribution Models",
                "Interpret channel impact and bias.",
                List.of("analytics", "product")),
            new StepTemplate("User Segmentation",
                "Behavioral clusters and lifecycle stages.",
                List.of("analytics", "machine-learning")),
            new StepTemplate("Forecasting Metrics",
                "Estimate growth and seasonality effects.",
                List.of("analytics", "machine-learning")),
            new StepTemplate("Instrumentation QA",
                "Validate events and data contracts.",
                List.of("testing", "analytics")),
            new StepTemplate("Data Storytelling",
                "Present insights for product decisions.",
                List.of("product", "analytics")),
            new StepTemplate("Decision Frameworks",
                "Prioritize roadmap with evidence.",
                List.of("product", "analytics")),
            new StepTemplate("Capstone: Product Insights Program",
                "End-to-end analytics loop for a product team.",
                List.of("product", "analytics", "sql"))
        ))
    );
  }

  private List<ItemSeed> buildLinearItems(List<StepTemplate> templates) {
    List<ItemSeed> result = new ArrayList<>();
    for (int i = 0; i < templates.size(); i++) {
      StepTemplate template = templates.get(i);
      String parentTitle = i == 0 ? null : templates.get(i - 1).title();

      ItemStatus status;
      if (i == 0) {
        status = ItemStatus.DONE;
      } else if (i == 1) {
        status = ItemStatus.IN_PROGRESS;
      } else {
        status = ItemStatus.PLANNED;
      }

      result.add(new ItemSeed(
          template.title(),
          template.details(),
          status,
          parentTitle,
          template.tagNames()));
    }
    return result;
  }

  private RoadMap ensureRoadMap(User owner, String title, String description) {
    RoadMap roadmap = roadMapRepository.findByTitle(title).stream()
        .findFirst()
        .orElseGet(RoadMap::new);

    roadmap.setTitle(title);
    roadmap.setDescription(description);
    roadmap.setOwner(owner);
    return roadMapRepository.save(roadmap);
  }

  private RoadMapItem ensureRoadMapItem(RoadMap roadMap,
                                        String title,
                                        String details,
                                        ItemStatus status,
                                        String parentTitle,
                                        Map<String, Tag> tagsByName,
                                        List<String> tagNames) {
    RoadMapItem item = roadMapItemRepository.findByRoadMapIdAndTitle(roadMap.getId(), title)
        .orElseGet(RoadMapItem::new);

    item.setRoadMap(roadMap);
    item.setTitle(title);
    item.setDetails(details);
    item.setStatus(status);
    item.setParentItem(resolveParent(roadMap, title, parentTitle));

    item.getTags().clear();
    for (String tagName : tagNames) {
      Tag tag = tagsByName.get(normalizeKey(tagName));
      if (tag != null) {
        item.getTags().add(tag);
      }
    }

    return roadMapItemRepository.save(item);
  }

  private RoadMapItem resolveParent(RoadMap roadMap, String itemTitle, String parentTitle) {
    if (parentTitle == null || parentTitle.isBlank()) {
      return null;
    }
    if (parentTitle.equals(itemTitle)) {
      return null;
    }
    return roadMapItemRepository.findByRoadMapIdAndTitle(roadMap.getId(), parentTitle)
        .orElse(null);
  }

  private void ensureCommentIfMissing(RoadMapItem item, User author, String content) {
    boolean exists = item.getComments().stream()
        .anyMatch(comment -> content.equals(comment.getContent()));
    if (exists) {
      return;
    }

    Comment comment = new Comment();
    comment.setContent(content);
    comment.setItem(item);
    comment.setAuthor(author);
    commentRepository.save(comment);
  }

  private String normalizeKey(String value) {
    return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
  }

  private record RoadmapSeed(String title, String description, List<ItemSeed> items) {
  }

  private record ItemSeed(String title, String details, ItemStatus status,
                          String parentTitle, List<String> tagNames) {
  }

  private record StepTemplate(String title, String details, List<String> tagNames) {
  }
}
