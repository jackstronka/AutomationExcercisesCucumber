// Jenkins CI – mirrors .github/workflows/ci.yml
//
// Supports both Linux and Windows agents (uses sh on Unix, bat on Windows).
//
// Requirements:
//   - JDK 21 and Maven 3+ on agent PATH (or configure in Global Tool Configuration and uncomment 'tools' block).
//   - Chrome: on Linux the pipeline can install it; on Windows install Chrome and ensure it is in PATH.
//
// Optional:
//   - Allure Report plugin: install it, then uncomment the allure(...) step in post {} to see report in Jenkins UI.
//   - Environment variable ALLURE_HISTORY_URL (e.g. https://user.github.io/repo): on Linux, downloads Allure history for trend charts.

pipeline {
    agent any

    options {
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
        timestamps()
    }

    environment {
        JAVA_VERSION = '21'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    // Standalone Pipeline often has no BRANCH_NAME; detached HEAD gives "HEAD". Detect main/master by ref.
                    if (env.BRANCH_NAME == null || env.BRANCH_NAME == '' || env.BRANCH_NAME == 'HEAD') {
                        if (isUnix()) {
                            def ref = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
                            def mainRef = sh(script: 'git rev-parse refs/remotes/origin/main 2>/dev/null || true', returnStdout: true).trim()
                            def masterRef = sh(script: 'git rev-parse refs/remotes/origin/master 2>/dev/null || true', returnStdout: true).trim()
                            env.BRANCH_NAME = (ref == mainRef ? 'main' : (ref == masterRef ? 'master' : (env.BRANCH_NAME ?: 'HEAD')))
                        } else {
                            def ref = bat(script: 'git rev-parse HEAD', returnStdout: true).trim()
                            def mainRef = bat(script: 'git rev-parse refs/remotes/origin/main', returnStdout: true).trim()
                            // Skip origin/master on Windows when repo has no master (avoids fatal error and script noise)
                            env.BRANCH_NAME = (ref == mainRef ? 'main' : (env.BRANCH_NAME ?: 'HEAD'))
                        }
                    }
                }
            }
        }

        stage('Set up Chrome') {
            when {
                expression { isUnix() }
            }
            steps {
                sh '''
                    if ! command -v google-chrome &> /dev/null; then
                        echo 'Installing Chrome...'
                        wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | sudo apt-key add -
                        echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" | sudo tee /etc/apt/sources.list.d/google-chrome.list
                        sudo apt-get update && sudo apt-get install -y google-chrome-stable
                    fi
                    google-chrome --version || true
                '''
            }
        }

        stage('Prepare Allure history') {
            steps {
                script {
                    if (isUnix()) {
                        sh '''
                            mkdir -p target/allure-report
                            if [ -n "${ALLURE_HISTORY_URL}" ]; then
                                wget -q -r -np -nH -e robots=off -P target/allure-report-dl "${ALLURE_HISTORY_URL}/history/" || true
                                HIST_DIR=$(find target/allure-report-dl -type d -name history 2>/dev/null | head -1)
                                if [ -n "$HIST_DIR" ] && [ -d "$HIST_DIR" ]; then
                                    mv "$HIST_DIR" target/allure-report/
                                fi
                                rm -rf target/allure-report-dl 2>/dev/null || true
                            fi
                        '''
                    } else {
                        bat 'if not exist target\\allure-report mkdir target\\allure-report'
                    }
                }
            }
        }

        stage('Test') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                }
            }
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn test -Pcucumber -B -Dheadless=true'
                        sh '''
                            echo "========== TEST SUMMARY (Surefire) =========="
                            for f in target/surefire-reports/*.txt; do [ -f "$f" ] && cat "$f"; done
                            echo "============================================="
                        '''
                    } else {
                        bat 'mvn test -Pcucumber -B -Dheadless=true'
                        bat '''
                            echo ========== TEST SUMMARY (Surefire) ==========
                            for %%f in (target\\surefire-reports\\*.txt) do type "%%f"
                            echo =============================================
                        '''
                    }
                }
            }
        }

        stage('Test (smoke)') {
            when {
                allOf {
                    not { anyOf { branch 'main'; branch 'master' } }
                }
            }
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn test -Pcucumber -B -Dheadless=true -Dcucumber.filter.tags=@smoke'
                        sh '''
                            echo "========== TEST SUMMARY (Surefire) =========="
                            for f in target/surefire-reports/*.txt; do [ -f "$f" ] && cat "$f"; done
                            echo "============================================="
                        '''
                    } else {
                        bat 'mvn test -Pcucumber -B -Dheadless=true -Dcucumber.filter.tags=@smoke'
                        bat '''
                            echo ========== TEST SUMMARY (Surefire) ==========
                            for %%f in (target\\surefire-reports\\*.txt) do type "%%f"
                            echo =============================================
                        '''
                    }
                }
            }
        }

        stage('Generate Allure report') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn allure:report -Pcucumber -B'
                    } else {
                        bat 'mvn allure:report -Pcucumber -B'
                    }
                }
            }
        }
    }

    post {
        always {
            // Uncomment after installing "Allure Report" plugin in Jenkins:
            // allure(includeProperties: false, jdk: '', results: [[path: 'target/allure-results']])
            archiveArtifacts(
                artifacts: 'target/surefire-reports/**,target/cucumber-reports.html,target/cucumber-report.json,target/allure-report/**',
                allowEmptyArchive: true,
                fingerprint: true
            )
        }
        failure {
            echo 'Pipeline or test stage failed.'
        }
    }
}
