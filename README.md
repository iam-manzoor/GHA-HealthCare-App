# GHA HealthCare App — GitHub Actions Demo

[![CI](https://github.com/<OWNER>/<REPO>/actions/workflows/ci.yml/badge.svg)](https://github.com/<OWNER>/<REPO>/actions/workflows/ci.yml)

Welcome to the GHA HealthCare App demo repository — a small multi-service Java/Maven microservices example designed to showcase a practical, production-minded GitHub Actions CI workflow.

This repository contains five lightweight Spring Boot services (appointment, doctor, patient, report, user). The goal of this demo is to illustrate a clean CI pipeline that builds, tests, packages, and optionally builds Docker images for each service.

**Highlights**
- Clean, matrix-based build: runs the same build/test steps for each service in parallel.
- Maven caching to speed up builds.
- Artifacts uploaded per-service for later inspection or release.
- Optional Docker image build and push to GitHub Container Registry (GHCR).
- Friendly documentation and a diagram showing the workflow flow.

**Why this README is special**
This README focuses on GitHub Actions: how the pipeline is structured, what each job does, and how to customize it for your project or organization.

**Files of interest**
- Workflow: [.github/workflows/ci.yml](.github/workflows/ci.yml)
- Services: `healthcare-microservices/*-service`

**Quick links**
- Build status: the badge above (replace `<OWNER>/<REPO>` with your repo path for an active badge)
- Workflow file: [.github/workflows/ci.yml](.github/workflows/ci.yml)

---

## Features and workflow overview

- Trigger on `push`, `pull_request`, manual `workflow_dispatch`, and a periodic `schedule`.
- Multi-job pipeline:
	- `build` job (matrix over services): checkout, setup JDK, cache Maven, run `mvn verify`, `mvn package`, upload artifact.
	- `docker` job (depends on `build`): build images using `docker buildx`, optionally login and push to GHCR on push events.

## Workflow diagram

```mermaid
flowchart TD
	A[commit / PR] --> B{CI Triggers};
	B --> C[Build Matrix per Service];
	C --> D[Test & Verify];
	D --> E[Package Artifacts];
	E --> F[Upload Artifacts];
	F --> G[Docker Build<br/>(optional)];
	G --> H[Push to GHCR<br/>(on push)];
```

## How the GitHub Actions workflow works (at a glance)

1. A push or PR starts the `build` job which runs concurrently for each service using a matrix.
2. Each matrix job:
	 - checks out the repo,
	 - sets up `temurin` JDK (Java 17),
	 - caches the Maven repository,
	 - runs `mvn -B clean verify` to run tests,
	 - runs `mvn -B package -DskipTests` to produce a packaged artifact,
	 - uploads the produced JAR as an action artifact for later inspection.
3. Once all build matrix jobs succeed, the `docker` job builds images for all services using `buildx` and can push them to GHCR when triggered during `push` events.

## Required secrets (for pushing images)

- `GITHUB_TOKEN` (provided automatically by GitHub Actions for basic actions/interactions)
- `GHCR_TOKEN` or use `GITHUB_TOKEN` for pushes to ghcr.io when allowed
- If you use Docker Hub: `DOCKER_USERNAME` and `DOCKER_PASSWORD`

## Local testing

You can run the workflow locally with `act` (a community tool that emulates GitHub Actions):

```bash
# install act: https://github.com/nektos/act
act -j build -P ubuntu-latest=nektos/act-environments-ubuntu:18.04
```

Note: running Docker image build steps locally requires Docker and the runner to support `docker` (use `--privileged`/`--container-runtime` flags per `act` docs).

---

## Example: customize the workflow

Open [.github/workflows/ci.yml](.github/workflows/ci.yml) and edit these sections:
- `matrix.service`: list services to build
- Java version in `actions/setup-java`
- Image registry and credentials in the `docker` job

## Contributing

This repo is a demo — contributions are welcome. If you improve a workflow step or add new automation patterns, please open a PR explaining the motivation and performance gains.

---

## Next steps

- Replace `<OWNER>/<REPO>` in the badge with your repo path to render the badge.
- Add `GHCR_TOKEN` or Docker credentials in repository Secrets to enable image pushes.
- Let me know if you want an alternate workflow that also publishes Maven artifacts to GitHub Packages, performs semantic-release tagging, or runs integration tests in Testcontainers.

Happy building! 🚀

