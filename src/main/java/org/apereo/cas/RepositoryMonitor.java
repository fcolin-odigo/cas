/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apereo.cas;

import org.apereo.cas.github.GitHubOperations;
import org.apereo.cas.github.Page;
import org.apereo.cas.github.PullRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Central class for monitoring the configured repository.
 *
 * @author Andy Wilkinson
 */
@Slf4j
class RepositoryMonitor {

    private static final int ONE_MINUTE = 60 * 1000;

    private final GitHubOperations gitHub;

    private final MonitoredRepository repository;

    private final List<PullRequestListener> pullRequestListeners;

    RepositoryMonitor(final GitHubOperations gitHub, final MonitoredRepository repository,
                      final List<PullRequestListener> pullRequestListeners) {
        this.gitHub = gitHub;
        this.repository = repository;
        this.pullRequestListeners = pullRequestListeners;
    }

    @Scheduled(fixedRate = ONE_MINUTE)
    void monitor() {
        log.info("Monitoring {}", this.repository.getFullName());
        try {
            log.info("Processing pull requests for {}", this.repository.getFullName());
            var page = this.gitHub.getPullRequests(this.repository.getOrganization(), this.repository.getName());
            while (page != null) {
                page.getContent()
                    .stream()
                    .filter(PullRequest::isOpen)
                    .forEach(pr -> pullRequestListeners.forEach(listener -> listener.onOpenPullRequest(pr)));
                page = page.next();
            }

            log.info("Processing workflow runs for {}", this.repository.getFullName());
            var currentBranches = this.repository.getActiveBranches();
            repository.cancelQualifyingWorkflowRuns(currentBranches);
            repository.removeCancelledWorkflowRuns();
        } catch (final Exception ex) {
            log.warn("A failure occurred during monitoring", ex);
        }
        log.info("Monitoring of {} completed", this.repository.getFullName());
    }

}
