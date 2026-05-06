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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!test")
@RequiredArgsConstructor
@Transactional
public class DataInitializer implements CommandLineRunner {

  private static final String DEMO_OWNER_EMAIL = "vladislav@example.com";
  private static final String DEMO_OWNER_LOGIN = "admin";
  private static final String DEMO_OWNER_SECRET = "admin123";
  private static final String DEMO_USER_EMAIL = "user@example.com";
  private static final String DEMO_USER_LOGIN = "user";
  private static final String DEMO_USER_SECRET = "user123";
  private static final String CATALOG_PREFIX = "Roadmap: ";
  private static final String TAG_SPRING = "spring";
  private static final String TAG_PYTHON = "python";
  private static final String TAG_MACHINE_LEARNING = "machine-learning";
  private static final String TAG_DEVOPS = "devops";
  private static final String TAG_JAVASCRIPT = "javascript";
  private static final String TAG_DEEP_LEARNING = "deep-learning";
  private static final String TAG_REACT = "react";
  private static final String TAG_FRONTEND = "frontend";
  private static final String TAG_KUBERNETES = "kubernetes";
  private static final String TAG_DOCKER = "docker";
  private static final String TAG_LINUX = "linux";
  private static final String TAG_CLOUD = "cloud";
  private static final String TAG_SECURITY = "security";
  private static final String TAG_DATA_ENGINEERING = "data-engineering";
  private static final String TAG_KAFKA = "kafka";
  private static final String TAG_AIRFLOW = "airflow";
  private static final String TAG_NOSQL = "nosql";
  private static final String TAG_MOBILE = "mobile";
  private static final String TAG_TESTING = "testing";
  private static final String TAG_MICROSERVICES = "microservices";
  private static final String TAG_ANDROID = "android";
  private static final String TAG_SYSTEM_DESIGN = "system-design";
  private static final String TAG_NETWORKING = "networking";
  private static final String TAG_ARCHITECTURE = "architecture";
  private static final String TAG_ANALYTICS = "analytics";
  private static final String TAG_PRODUCT = "product";
  private static final String TAG_OBSERVABILITY = "observability";
  private static final String TAG_MLOPS = "mlops";
  private static final String TAG_CI_CD = "ci-cd";

  private final UserRepository userRepository;
  private final TagRepository tagRepository;
  private final RoadMapRepository roadMapRepository;
  private final RoadMapItemRepository roadMapItemRepository;
  private final CommentRepository commentRepository;
  private final JdbcTemplate jdbcTemplate;

  @Override
  public void run(String... args) {
    cleanupGeneratedTags();
    User demoOwner = ensureDemoOwner();
    ensureRegularUser();
    Map<String, Tag> tagsByName = ensureTags();

    seedJavaBackendRoadmap(demoOwner, tagsByName);
    seedCatalogRoadmaps(demoOwner, tagsByName);
  }

  private void cleanupGeneratedTags() {
    jdbcTemplate.update(
        """
            delete from roadmap_item_tag
            where tag_id in (
                select id
                from tags
                where lower(name) like 'tag-check-%'
                   or lower(name) like 'tmgr-tag-%'
                   or name ~* '^tag-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$'
            )
            """
    );

    jdbcTemplate.update(
        """
            delete from tags
            where lower(name) like 'tag-check-%'
               or lower(name) like 'tmgr-tag-%'
               or name ~* '^tag-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$'
            """
    );
  }

  private User ensureDemoOwner() {
    User user = userRepository.findByEmailIgnoreCase(DEMO_OWNER_EMAIL)
        .orElseGet(() -> {
          User entity = new User();
          entity.setFirstName("Vladislav");
          entity.setLastName("Mogilny");
          entity.setEmail(DEMO_OWNER_EMAIL);
          return entity;
        });

    boolean changed = false;
    if (user.getLogin() == null || user.getLogin().isBlank()) {
      user.setLogin(DEMO_OWNER_LOGIN);
      changed = true;
    }
    if (user.getPassword() == null || user.getPassword().isBlank()) {
      user.setPassword(DEMO_OWNER_SECRET);
      changed = true;
    }
    if (user.getId() == null || changed) {
      return userRepository.save(user);
    }
    return user;
  }

  private User ensureRegularUser() {
    User user = userRepository.findByEmailIgnoreCase(DEMO_USER_EMAIL)
        .or(() -> userRepository.findByLoginIgnoreCase(DEMO_USER_LOGIN))
        .orElseGet(() -> {
          User entity = new User();
          entity.setFirstName("Regular");
          entity.setLastName("User");
          entity.setEmail(DEMO_USER_EMAIL);
          return entity;
        });

    boolean changed = false;
    if (!DEMO_USER_EMAIL.equalsIgnoreCase(user.getEmail())) {
      user.setEmail(DEMO_USER_EMAIL);
      changed = true;
    }
    if (!DEMO_USER_LOGIN.equalsIgnoreCase(user.getLogin())) {
      user.setLogin(DEMO_USER_LOGIN);
      changed = true;
    }
    if (!DEMO_USER_SECRET.equals(user.getPassword())) {
      user.setPassword(DEMO_USER_SECRET);
      changed = true;
    }

    if (user.getId() == null || changed) {
      return userRepository.save(user);
    }
    return user;
  }

  private Map<String, Tag> ensureTags() {
    List<String> names = List.of(
        TAG_SPRING, "sql", "java", TAG_PYTHON, "math", TAG_MACHINE_LEARNING,
        TAG_DEEP_LEARNING, TAG_FRONTEND, TAG_JAVASCRIPT, TAG_REACT, TAG_DEVOPS,
        TAG_DOCKER, TAG_KUBERNETES, TAG_SECURITY, TAG_CLOUD, TAG_LINUX, "git",
        TAG_DATA_ENGINEERING, "etl", TAG_KAFKA, "spark", TAG_AIRFLOW, TAG_NOSQL,
        TAG_MICROSERVICES, TAG_TESTING, TAG_ANDROID, "ios", "swift", "kotlin", TAG_MOBILE,
        TAG_NETWORKING, "cryptography", TAG_SYSTEM_DESIGN, TAG_ARCHITECTURE,
        TAG_PRODUCT, TAG_ANALYTICS, TAG_OBSERVABILITY, TAG_CI_CD, TAG_MLOPS, "nlp", "cv");

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
        List.of("java", TAG_SPRING, "sql"));

    RoadMapItem transactions = ensureRoadMapItem(
        roadmap,
        "Master transactions",
        "Understand transactional boundaries and rollback behavior",
        ItemStatus.PLANNED,
        "Learn JPA basics",
        tagsByName,
        List.of(TAG_SPRING, "sql"));

    ensureRoadMapItem(
        roadmap,
        "Handle async background tasks",
        "Implement task polling and status tracking",
        ItemStatus.PLANNED,
        "Master transactions",
        tagsByName,
        List.of(TAG_SPRING, "java"));

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
                List.of(TAG_MACHINE_LEARNING, TAG_PRODUCT)),
            new StepTemplate("Python Foundations",
                "Core syntax, data structures, functions, and OOP.",
                List.of(TAG_PYTHON)),
            new StepTemplate("Git, CLI, and Environment Setup",
                "Version control and reproducible development workflow.",
                List.of("git", TAG_LINUX)),
            new StepTemplate("NumPy and Vectorized Computing",
                "Arrays, broadcasting, and numerical operations.",
                List.of(TAG_PYTHON, "math")),
            new StepTemplate("Pandas for Data Analysis",
                "Data cleaning, joins, and table transformations.",
                List.of(TAG_PYTHON, TAG_ANALYTICS)),
            new StepTemplate("SQL for Analytical Workloads",
                "Filtering, aggregations, window functions.",
                List.of("sql", TAG_ANALYTICS)),
            new StepTemplate("Data Visualization",
                "Plotting distributions and relationships.",
                List.of(TAG_PYTHON, TAG_ANALYTICS)),
            new StepTemplate("Linear Algebra Essentials",
                "Vectors, matrices, rank, decompositions.",
                List.of("math", TAG_MACHINE_LEARNING)),
            new StepTemplate("Calculus for Optimization",
                "Derivatives, gradients, and chain rule intuition.",
                List.of("math", TAG_MACHINE_LEARNING)),
            new StepTemplate("Probability and Statistics",
                "Random variables, distributions, confidence intervals.",
                List.of("math", TAG_MACHINE_LEARNING)),
            new StepTemplate("Feature Engineering Basics",
                "Encoding, scaling, and robust feature design.",
                List.of(TAG_MACHINE_LEARNING, TAG_ANALYTICS)),
            new StepTemplate("Train/Validation/Test Strategy",
                "Avoiding leakage and overfitting in experiments.",
                List.of(TAG_MACHINE_LEARNING, TAG_TESTING)),
            new StepTemplate("Linear Regression",
                "Baseline regression model and diagnostics.",
                List.of(TAG_MACHINE_LEARNING)),
            new StepTemplate("Logistic Regression",
                "Binary classification and probability outputs.",
                List.of(TAG_MACHINE_LEARNING)),
            new StepTemplate("Model Metrics and Thresholds",
                "Accuracy, precision/recall, ROC-AUC.",
                List.of(TAG_MACHINE_LEARNING, TAG_ANALYTICS)),
            new StepTemplate("Decision Trees",
                "Interpretable tree-based models.",
                List.of(TAG_MACHINE_LEARNING)),
            new StepTemplate("Random Forest and Bagging",
                "Variance reduction via ensembles.",
                List.of(TAG_MACHINE_LEARNING)),
            new StepTemplate("Gradient Boosting",
                "XGBoost/LightGBM/CatBoost concepts.",
                List.of(TAG_MACHINE_LEARNING)),
            new StepTemplate("Hyperparameter Optimization",
                "Grid/random search and Bayesian approaches.",
                List.of(TAG_MACHINE_LEARNING, TAG_TESTING)),
            new StepTemplate("Unsupervised Learning",
                "Clustering and dimensionality reduction.",
                List.of(TAG_MACHINE_LEARNING)),
            new StepTemplate("Pipelines and Reproducibility",
                "Feature + model pipelines with stable experiments.",
                List.of(TAG_MACHINE_LEARNING, TAG_MLOPS)),
            new StepTemplate("Introduction to Neural Networks",
                "Perceptron, activations, feed-forward networks.",
                List.of(TAG_DEEP_LEARNING, TAG_MACHINE_LEARNING)),
            new StepTemplate("Backpropagation and Optimization",
                "Gradients, SGD, Adam, learning rates.",
                List.of(TAG_DEEP_LEARNING, "math")),
            new StepTemplate("PyTorch Fundamentals",
                "Tensors, autograd, and training loops.",
                List.of(TAG_DEEP_LEARNING, TAG_PYTHON)),
            new StepTemplate("Regularization Techniques",
                "Dropout, weight decay, early stopping.",
                List.of(TAG_DEEP_LEARNING, TAG_TESTING)),
            new StepTemplate("Computer Vision Fundamentals",
                "Image preprocessing and convolution basics.",
                List.of("cv", TAG_DEEP_LEARNING)),
            new StepTemplate("CNN Architectures",
                "ResNet, transfer learning, fine-tuning.",
                List.of("cv", TAG_DEEP_LEARNING)),
            new StepTemplate("Natural Language Processing Basics",
                "Tokenization, embeddings, sequence models.",
                List.of("nlp", TAG_DEEP_LEARNING)),
            new StepTemplate("Transformer Architecture",
                "Attention mechanism and modern NLP models.",
                List.of("nlp", TAG_DEEP_LEARNING)),
            new StepTemplate("Time Series Forecasting",
                "Trend/seasonality models and backtesting.",
                List.of(TAG_MACHINE_LEARNING, TAG_ANALYTICS)),
            new StepTemplate("Experiment Tracking",
                "Track metrics, params, and artifacts.",
                List.of(TAG_MLOPS, TAG_OBSERVABILITY)),
            new StepTemplate("Data Versioning",
                "Version datasets and feature snapshots.",
                List.of(TAG_DATA_ENGINEERING, TAG_MLOPS)),
            new StepTemplate("Model Packaging",
                "Containerize model with reproducible runtime.",
                List.of(TAG_DOCKER, TAG_MLOPS)),
            new StepTemplate("Serving Inference API",
                "Expose prediction endpoint and contracts.",
                List.of(TAG_MLOPS, TAG_MICROSERVICES)),
            new StepTemplate("Batch and Streaming Inference",
                "Design online/offline prediction paths.",
                List.of(TAG_MLOPS, TAG_KAFKA)),
            new StepTemplate("Model Monitoring",
                "Track drift, latency, and prediction quality.",
                List.of(TAG_OBSERVABILITY, TAG_MLOPS)),
            new StepTemplate("Feature Store Concepts",
                "Consistency between training and serving features.",
                List.of(TAG_DATA_ENGINEERING, TAG_MLOPS)),
            new StepTemplate("CI/CD for ML",
                "Automate tests, model build and deployment.",
                List.of(TAG_CI_CD, TAG_MLOPS)),
            new StepTemplate("Kubernetes Deployment",
                "Deploy scalable model services.",
                List.of(TAG_KUBERNETES, TAG_CLOUD)),
            new StepTemplate("Responsible AI",
                "Bias, fairness, explainability and governance.",
                List.of(TAG_MACHINE_LEARNING, TAG_PRODUCT)),
            new StepTemplate("A/B Testing for ML Products",
                "Online evaluation and rollout strategy.",
                List.of(TAG_ANALYTICS, TAG_PRODUCT)),
            new StepTemplate("Production Incident Playbook",
                "Diagnose model failures and rollback fast.",
                List.of(TAG_OBSERVABILITY, TAG_DEVOPS)),
            new StepTemplate("Capstone: End-to-End ML System",
                "From data ingestion to monitored production model.",
                List.of(TAG_MACHINE_LEARNING, TAG_MLOPS, TAG_CLOUD))
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
                List.of(TAG_LINUX)),
            new StepTemplate("Python for ETL",
                "File processing, API ingestion, automation scripts.",
                List.of(TAG_PYTHON, "etl")),
            new StepTemplate("SQL Advanced Patterns",
                "Window functions, CTEs, query optimization.",
                List.of("sql", TAG_ANALYTICS)),
            new StepTemplate("Data Modeling Fundamentals",
                "Normalization, star schema, dimensions/facts.",
                List.of(TAG_DATA_ENGINEERING, TAG_ANALYTICS)),
            new StepTemplate("Batch Pipeline Design",
                "DAG-oriented ETL and idempotent jobs.",
                List.of("etl", TAG_DATA_ENGINEERING)),
            new StepTemplate("Airflow Orchestration",
                "Schedules, dependencies, retries, monitoring.",
                List.of(TAG_AIRFLOW, "etl")),
            new StepTemplate("Distributed Processing with Spark",
                "Partitioning, joins, and performance tuning.",
                List.of("spark", TAG_DATA_ENGINEERING)),
            new StepTemplate("Data Lake Fundamentals",
                "Object storage, partitioning, and file formats.",
                List.of(TAG_CLOUD, TAG_DATA_ENGINEERING)),
            new StepTemplate("Warehouse Fundamentals",
                "Columnar storage and BI workloads.",
                List.of(TAG_DATA_ENGINEERING, TAG_ANALYTICS)),
            new StepTemplate("Streaming Concepts",
                "Event-driven architecture and consumer groups.",
                List.of(TAG_KAFKA, TAG_DATA_ENGINEERING)),
            new StepTemplate("Kafka in Practice",
                "Topics, retention, schema evolution.",
                List.of(TAG_KAFKA, TAG_DATA_ENGINEERING)),
            new StepTemplate("Change Data Capture",
                "Capture database updates to event streams.",
                List.of(TAG_KAFKA, "sql")),
            new StepTemplate("Data Quality Checks",
                "Schema tests, null checks, anomaly detection.",
                List.of(TAG_TESTING, TAG_DATA_ENGINEERING)),
            new StepTemplate("Observability for Pipelines",
                "Logging, metrics, alerting for SLA.",
                List.of(TAG_OBSERVABILITY, TAG_DEVOPS)),
            new StepTemplate("Containerized Jobs",
                "Run ETL in Docker images.",
                List.of(TAG_DOCKER, "etl")),
            new StepTemplate("Infrastructure as Code",
                "Provision cloud resources declaratively.",
                List.of(TAG_CLOUD, TAG_DEVOPS)),
            new StepTemplate("Data Security and Governance",
                "Access control, lineage, retention policies.",
                List.of(TAG_SECURITY, TAG_DATA_ENGINEERING)),
            new StepTemplate("Cost Optimization",
                "Control compute/storage and optimize queries.",
                List.of(TAG_CLOUD, TAG_ANALYTICS)),
            new StepTemplate("CI/CD for Pipelines",
                "Automate deploys and migration checks.",
                List.of(TAG_CI_CD, TAG_DATA_ENGINEERING)),
            new StepTemplate("Capstone: Streaming + Batch Platform",
                "Unified data platform with quality and monitoring.",
                List.of(TAG_DATA_ENGINEERING, TAG_KAFKA, TAG_AIRFLOW))
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
                List.of(TAG_SPRING, "java")),
            new StepTemplate("REST API Design",
                "Resource modeling and HTTP semantics.",
                List.of(TAG_MICROSERVICES, "java")),
            new StepTemplate("Validation and Error Handling",
                "Robust contracts and predictable responses.",
                List.of(TAG_SPRING, TAG_TESTING)),
            new StepTemplate("SQL and Indexing",
                "Schema design and query performance.",
                List.of("sql", "java")),
            new StepTemplate("JPA and Transactions",
                "Entity mapping and consistency boundaries.",
                List.of(TAG_SPRING, "sql")),
            new StepTemplate("Caching Strategies",
                "Reduce latency and pressure on DB.",
                List.of(TAG_MICROSERVICES, "java")),
            new StepTemplate("Messaging and Async Processing",
                "Background tasks and eventual consistency.",
                List.of(TAG_KAFKA, TAG_MICROSERVICES)),
            new StepTemplate("Authentication and Authorization",
                "Token-based auth and role model.",
                List.of(TAG_SECURITY, TAG_SPRING)),
            new StepTemplate("Testing Pyramid",
                "Unit, integration, contract testing.",
                List.of(TAG_TESTING, "java")),
            new StepTemplate("API Documentation",
                "OpenAPI and consumer-friendly docs.",
                List.of(TAG_SPRING, TAG_PRODUCT)),
            new StepTemplate("Docker for Backend",
                "Package and run services in containers.",
                List.of(TAG_DOCKER, TAG_DEVOPS)),
            new StepTemplate("Observability",
                "Structured logs, metrics, tracing.",
                List.of(TAG_OBSERVABILITY, TAG_DEVOPS)),
            new StepTemplate("System Reliability",
                "Timeouts, retries, circuit breaker patterns.",
                List.of(TAG_ARCHITECTURE, TAG_MICROSERVICES)),
            new StepTemplate("Horizontal Scaling",
                "Stateless services and load balancing.",
                List.of(TAG_CLOUD, TAG_ARCHITECTURE)),
            new StepTemplate("CI/CD",
                "Automated pipelines and quality gates.",
                List.of(TAG_CI_CD, TAG_DEVOPS)),
            new StepTemplate("Capstone: Production Backend Service",
                "Secure, monitored, deployable backend application.",
                List.of("java", TAG_SPRING, TAG_CLOUD))
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
                List.of(TAG_FRONTEND)),
            new StepTemplate("CSS Foundations",
                "Layout systems, responsive UI, component styles.",
                List.of(TAG_FRONTEND)),
            new StepTemplate("JavaScript Core",
                "Language fundamentals, async, modules.",
                List.of(TAG_JAVASCRIPT)),
            new StepTemplate("TypeScript Basics",
                "Safer APIs and typed components.",
                List.of(TAG_JAVASCRIPT, TAG_FRONTEND)),
            new StepTemplate("React Fundamentals",
                "State, props, hooks and composition.",
                List.of(TAG_REACT, TAG_FRONTEND)),
            new StepTemplate("Routing and Navigation",
                "Client-side route architecture.",
                List.of(TAG_REACT, TAG_FRONTEND)),
            new StepTemplate("State Management",
                "Server/client state patterns.",
                List.of(TAG_REACT, TAG_JAVASCRIPT)),
            new StepTemplate("Forms and Validation",
                "Reliable input handling and UX.",
                List.of(TAG_FRONTEND, TAG_TESTING)),
            new StepTemplate("Performance Optimization",
                "Memoization, splitting, and rendering strategy.",
                List.of(TAG_FRONTEND, TAG_REACT)),
            new StepTemplate("Testing Frontend",
                "Unit, integration and e2e tests.",
                List.of(TAG_TESTING, TAG_FRONTEND)),
            new StepTemplate("Design System",
                "Reusable components and tokens.",
                List.of(TAG_FRONTEND, TAG_PRODUCT)),
            new StepTemplate("Accessibility",
                "Keyboard navigation and semantic interactions.",
                List.of(TAG_FRONTEND, TAG_PRODUCT)),
            new StepTemplate("Frontend Security",
                "XSS, CSP and safe data rendering.",
                List.of(TAG_SECURITY, TAG_FRONTEND)),
            new StepTemplate("Monitoring and RUM",
                "Track errors and UX performance in production.",
                List.of(TAG_OBSERVABILITY, TAG_FRONTEND)),
            new StepTemplate("Build and Bundling",
                "Vite/Webpack optimization and delivery.",
                List.of(TAG_FRONTEND, TAG_DEVOPS)),
            new StepTemplate("Capstone: Production SPA",
                "Large frontend with robust architecture and testing.",
                List.of(TAG_REACT, TAG_FRONTEND, TAG_CI_CD))
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
                List.of(TAG_LINUX, TAG_DEVOPS)),
            new StepTemplate("Networking Fundamentals",
                "TCP/IP, DNS, HTTP, TLS basics.",
                List.of(TAG_NETWORKING, TAG_DEVOPS)),
            new StepTemplate("Bash and Automation",
                "Scripting repetitive ops tasks.",
                List.of(TAG_LINUX, TAG_DEVOPS)),
            new StepTemplate("Git Workflows",
                "Branch strategy and release flow.",
                List.of("git", TAG_DEVOPS)),
            new StepTemplate("Docker",
                "Images, layers, runtime and registries.",
                List.of(TAG_DOCKER, TAG_DEVOPS)),
            new StepTemplate("Kubernetes Basics",
                "Pods, deployments, services, namespaces.",
                List.of(TAG_KUBERNETES, TAG_DEVOPS)),
            new StepTemplate("Kubernetes Operations",
                "Autoscaling, config, secrets, rollout strategies.",
                List.of(TAG_KUBERNETES, TAG_DEVOPS)),
            new StepTemplate("Ingress and API Gateways",
                "Traffic management and service exposure.",
                List.of(TAG_KUBERNETES, TAG_NETWORKING)),
            new StepTemplate("CI/CD Pipelines",
                "Build, test, deploy automation.",
                List.of(TAG_CI_CD, TAG_DEVOPS)),
            new StepTemplate("Infrastructure as Code",
                "Versioned infrastructure provisioning.",
                List.of(TAG_CLOUD, TAG_DEVOPS)),
            new StepTemplate("Cloud Fundamentals",
                "Compute, storage, networking primitives.",
                List.of(TAG_CLOUD, TAG_DEVOPS)),
            new StepTemplate("Observability Stack",
                "Metrics, logs, tracing and alerting.",
                List.of(TAG_OBSERVABILITY, TAG_DEVOPS)),
            new StepTemplate("SRE Practices",
                "SLO, error budgets, postmortems.",
                List.of(TAG_OBSERVABILITY, TAG_ARCHITECTURE)),
            new StepTemplate("Security Hardening",
                "Least privilege, secrets, vulnerability scanning.",
                List.of(TAG_SECURITY, TAG_DEVOPS)),
            new StepTemplate("Backup and Disaster Recovery",
                "Recovery strategy and chaos drills.",
                List.of(TAG_CLOUD, TAG_SECURITY)),
            new StepTemplate("Cost and Capacity Management",
                "Rightsizing and scaling economics.",
                List.of(TAG_CLOUD, TAG_ANALYTICS)),
            new StepTemplate("Incident Response",
                "Runbooks, escalation and communication.",
                List.of(TAG_OBSERVABILITY, TAG_DEVOPS)),
            new StepTemplate("Capstone: Production DevOps Stack",
                "Automated, secure, observable deployment platform.",
                List.of(TAG_DEVOPS, TAG_KUBERNETES, TAG_CI_CD))
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
                List.of(TAG_SECURITY)),
            new StepTemplate("Networking for Security",
                "Protocols, packet flow, segmentation.",
                List.of(TAG_NETWORKING, TAG_SECURITY)),
            new StepTemplate("Operating System Security",
                "Hardening Linux and Windows baselines.",
                List.of(TAG_LINUX, TAG_SECURITY)),
            new StepTemplate("Identity and Access Management",
                "Authentication, authorization, MFA.",
                List.of(TAG_SECURITY, TAG_CLOUD)),
            new StepTemplate("Cryptography Basics",
                "Hashing, symmetric/asymmetric encryption.",
                List.of("cryptography", TAG_SECURITY)),
            new StepTemplate("Secure Development Lifecycle",
                "Shift-left security in delivery pipeline.",
                List.of(TAG_SECURITY, TAG_CI_CD)),
            new StepTemplate("Web Security",
                "OWASP Top 10 and mitigation practices.",
                List.of(TAG_SECURITY, TAG_FRONTEND)),
            new StepTemplate("API Security",
                "Auth flows, rate limiting, abuse prevention.",
                List.of(TAG_SECURITY, TAG_MICROSERVICES)),
            new StepTemplate("Cloud Security",
                "Cloud posture management and guardrails.",
                List.of(TAG_CLOUD, TAG_SECURITY)),
            new StepTemplate("Container Security",
                "Image scanning and runtime policies.",
                List.of(TAG_DOCKER, TAG_KUBERNETES, TAG_SECURITY)),
            new StepTemplate("SIEM and Log Analysis",
                "Correlate events and detect anomalies.",
                List.of(TAG_OBSERVABILITY, TAG_SECURITY)),
            new StepTemplate("Threat Modeling",
                "Identify assets, threats, and controls.",
                List.of(TAG_SECURITY, TAG_ARCHITECTURE)),
            new StepTemplate("Vulnerability Management",
                "Scan, prioritize, and remediate findings.",
                List.of(TAG_SECURITY, TAG_DEVOPS)),
            new StepTemplate("Incident Response",
                "Detection, containment, eradication, recovery.",
                List.of(TAG_SECURITY, TAG_DEVOPS)),
            new StepTemplate("Penetration Testing Basics",
                "Ethical testing methodology and reporting.",
                List.of(TAG_SECURITY, TAG_TESTING)),
            new StepTemplate("Capstone: Security Program",
                "Complete controls map with monitoring and response.",
                List.of(TAG_SECURITY, TAG_OBSERVABILITY))
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
                List.of(TAG_PRODUCT, TAG_FRONTEND)),
            new StepTemplate("Kotlin Essentials",
                "Language basics for Android development.",
                List.of("kotlin", TAG_ANDROID)),
            new StepTemplate("Android UI and Lifecycle",
                "Activities, fragments, lifecycle handling.",
                List.of(TAG_ANDROID, TAG_FRONTEND)),
            new StepTemplate("Swift Essentials",
                "Language basics for iOS development.",
                List.of("swift", "ios")),
            new StepTemplate("iOS UI and Lifecycle",
                "View controllers and lifecycle events.",
                List.of("ios", TAG_FRONTEND)),
            new StepTemplate("API Integration",
                "Networking, retries, and offline strategies.",
                List.of(TAG_MOBILE, TAG_MICROSERVICES)),
            new StepTemplate("Persistence on Device",
                "Local storage and synchronization patterns.",
                List.of(TAG_MOBILE, TAG_NOSQL)),
            new StepTemplate("Authentication on Mobile",
                "Secure token handling and biometric auth.",
                List.of(TAG_SECURITY, TAG_MOBILE)),
            new StepTemplate("Performance and Battery",
                "Rendering and background optimization.",
                List.of(TAG_MOBILE, TAG_ANALYTICS)),
            new StepTemplate("Push Notifications",
                "Reliable delivery and user engagement.",
                List.of(TAG_MOBILE, TAG_CLOUD)),
            new StepTemplate("Mobile Testing",
                "Unit, UI, and device testing matrix.",
                List.of(TAG_TESTING, TAG_MOBILE)),
            new StepTemplate("App Store Delivery",
                "Signing, release channels, rollout safety.",
                List.of(TAG_CI_CD, TAG_MOBILE)),
            new StepTemplate("Crash Monitoring",
                "Production diagnostics and alerting.",
                List.of(TAG_OBSERVABILITY, TAG_MOBILE)),
            new StepTemplate("Capstone: Production Mobile App",
                "Release-ready app with monitoring and CI/CD.",
                List.of(TAG_ANDROID, "ios", TAG_CI_CD))
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
                List.of(TAG_TESTING)),
            new StepTemplate("Test Case Design",
                "Boundary, equivalence, risk-based coverage.",
                List.of(TAG_TESTING, TAG_PRODUCT)),
            new StepTemplate("API Testing Basics",
                "Request/response validation and contracts.",
                List.of(TAG_TESTING, TAG_MICROSERVICES)),
            new StepTemplate("UI Testing Basics",
                "Page objects and stable selectors.",
                List.of(TAG_TESTING, TAG_FRONTEND)),
            new StepTemplate("Java for Test Automation",
                "Core language for automation framework.",
                List.of("java", TAG_TESTING)),
            new StepTemplate("Selenium and WebDriver",
                "Browser automation and synchronization.",
                List.of(TAG_TESTING, TAG_FRONTEND)),
            new StepTemplate("Playwright",
                "Modern end-to-end test implementation.",
                List.of(TAG_TESTING, TAG_JAVASCRIPT)),
            new StepTemplate("Contract Testing",
                "Service contracts between teams.",
                List.of(TAG_TESTING, TAG_MICROSERVICES)),
            new StepTemplate("Performance Testing",
                "Load, stress and bottleneck analysis.",
                List.of(TAG_TESTING, TAG_OBSERVABILITY)),
            new StepTemplate("Security Testing Basics",
                "Automated checks for common vulnerabilities.",
                List.of(TAG_SECURITY, TAG_TESTING)),
            new StepTemplate("Test Data Management",
                "Reliable environments and fixtures.",
                List.of(TAG_TESTING, TAG_DATA_ENGINEERING)),
            new StepTemplate("CI Integration for Tests",
                "Run suites in pipeline with quality gates.",
                List.of(TAG_CI_CD, TAG_TESTING)),
            new StepTemplate("Flaky Test Reduction",
                "Stabilize tests and improve trust.",
                List.of(TAG_TESTING, TAG_DEVOPS)),
            new StepTemplate("Capstone: Full QA Pipeline",
                "Automated quality process from API to UI.",
                List.of(TAG_TESTING, TAG_CI_CD))
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
                List.of(TAG_SYSTEM_DESIGN, TAG_ARCHITECTURE)),
            new StepTemplate("Capacity Estimation",
                "Traffic, storage, and throughput calculations.",
                List.of(TAG_SYSTEM_DESIGN, TAG_ANALYTICS)),
            new StepTemplate("Data Modeling for Scale",
                "Consistency trade-offs and access patterns.",
                List.of(TAG_SYSTEM_DESIGN, TAG_DATA_ENGINEERING)),
            new StepTemplate("Caching Layers",
                "Cache-aside and invalidation strategies.",
                List.of(TAG_SYSTEM_DESIGN, TAG_MICROSERVICES)),
            new StepTemplate("Load Balancing",
                "L4/L7 balancing and failover options.",
                List.of(TAG_SYSTEM_DESIGN, TAG_NETWORKING)),
            new StepTemplate("Message Queues and Streams",
                "Async decoupling and event-driven design.",
                List.of(TAG_KAFKA, TAG_SYSTEM_DESIGN)),
            new StepTemplate("Database Sharding",
                "Horizontal scaling and key strategy.",
                List.of("sql", TAG_SYSTEM_DESIGN)),
            new StepTemplate("NoSQL Patterns",
                "Document/key-value models for scale.",
                List.of(TAG_NOSQL, TAG_SYSTEM_DESIGN)),
            new StepTemplate("Observability Architecture",
                "Distributed tracing and telemetry.",
                List.of(TAG_OBSERVABILITY, TAG_ARCHITECTURE)),
            new StepTemplate("Reliability and Resilience",
                "Retries, timeouts, backpressure.",
                List.of(TAG_ARCHITECTURE, TAG_DEVOPS)),
            new StepTemplate("Security by Design",
                "Threat boundaries and defense in depth.",
                List.of(TAG_SECURITY, TAG_ARCHITECTURE)),
            new StepTemplate("Global Deployment",
                "Multi-region, latency and DR planning.",
                List.of(TAG_CLOUD, TAG_SYSTEM_DESIGN)),
            new StepTemplate("Design Review Practice",
                "Communicate trade-offs and alternatives.",
                List.of(TAG_SYSTEM_DESIGN, TAG_PRODUCT)),
            new StepTemplate("Capstone: Large-Scale Service Design",
                "Complete architecture for a high-load product.",
                List.of(TAG_SYSTEM_DESIGN, TAG_ARCHITECTURE))
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
                List.of(TAG_ANALYTICS, TAG_PRODUCT)),
            new StepTemplate("Event Taxonomy",
                "Consistent event naming and properties.",
                List.of(TAG_ANALYTICS, TAG_PRODUCT)),
            new StepTemplate("SQL for Product Metrics",
                "Build retention and conversion queries.",
                List.of("sql", TAG_ANALYTICS)),
            new StepTemplate("Dashboard Design",
                "Actionable visuals and monitoring boards.",
                List.of(TAG_ANALYTICS, TAG_PRODUCT)),
            new StepTemplate("A/B Testing Fundamentals",
                "Experiment design and significance.",
                List.of(TAG_ANALYTICS, TAG_PRODUCT)),
            new StepTemplate("Attribution Models",
                "Interpret channel impact and bias.",
                List.of(TAG_ANALYTICS, TAG_PRODUCT)),
            new StepTemplate("User Segmentation",
                "Behavioral clusters and lifecycle stages.",
                List.of(TAG_ANALYTICS, TAG_MACHINE_LEARNING)),
            new StepTemplate("Forecasting Metrics",
                "Estimate growth and seasonality effects.",
                List.of(TAG_ANALYTICS, TAG_MACHINE_LEARNING)),
            new StepTemplate("Instrumentation QA",
                "Validate events and data contracts.",
                List.of(TAG_TESTING, TAG_ANALYTICS)),
            new StepTemplate("Data Storytelling",
                "Present insights for product decisions.",
                List.of(TAG_PRODUCT, TAG_ANALYTICS)),
            new StepTemplate("Decision Frameworks",
                "Prioritize roadmap with evidence.",
                List.of(TAG_PRODUCT, TAG_ANALYTICS)),
            new StepTemplate("Capstone: Product Insights Program",
                "End-to-end analytics loop for a product team.",
                List.of(TAG_PRODUCT, TAG_ANALYTICS, "sql"))
        ))
    );
  }

  private List<ItemSeed> buildLinearItems(List<StepTemplate> templates) {
    List<ItemSeed> result = new ArrayList<>();
    for (int i = 0; i < templates.size(); i++) {
      StepTemplate template = templates.get(i);
      String parentTitle = i == 0 ? null : templates.get(i - 1).title();

      ItemStatus status = switch (i) {
        case 0 -> ItemStatus.DONE;
        case 1 -> ItemStatus.IN_PROGRESS;
        default -> ItemStatus.PLANNED;
      };

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
