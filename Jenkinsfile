// Jenkins CI – mirrors .github/workflows/ci.yml
//
// Requirements:
//   - JDK 21 and Maven 3+ on agent PATH, or configure in Jenkins (Global Tool Configuration)
//     and uncomment the 'tools' block below with your tool names.
//   - Chrome: on Linux the pipeline can install it; on Windows install Chrome and ensure it is in PATH.
//
// Optional:
//   - Allure Report plugin: for post-build Allure report in Jenkins UI.
//   - Environment variable ALLURE_HISTORY_URL (e.g. https://user.github.io/repo): downloads Allure history for trend charts.

pipeline {
    agent any

    options {
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
        timestamps()
        // buildDiscarder(logRotator(numToKeepStr: '20'))  // optional: keep last 20 builds
    }

    // Uncomment and set tool names from Jenkins Global Tool Configuration if you use it:
    // tools {
    //     maven 'Maven-3.9'
    //     jdk 'JDK-21'
    // }

    environment {
        JAVA_VERSION = '21'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
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
                sh 'mvn test -Pcucumber -B -Dheadless=true'
            }
        }

        stage('Test (smoke)') {
            when {
                allOf {
                    not { anyOf { branch 'main'; branch 'master' } }
                }
            }
            steps {
                sh 'mvn test -Pcucumber -B -Dheadless=true -Dcucumber.filter.tags=@smoke'
            }
        }

        stage('Generate Allure report') {
            steps {
                sh 'mvn allure:report -Pcucumber -B'
            }
        }
    }

    post {
        always {
            allure(
                includeProperties: false,
                jdk: '',
                results: [[path: 'target/allure-results']]
            )
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
