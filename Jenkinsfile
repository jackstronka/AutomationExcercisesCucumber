// Jenkins CI – mirrors .github/workflows/ci.yml
// Requirements: JDK 21, Maven 3+, Chrome (or use Linux agent; stage installs Chrome on Ubuntu).
// Optional: Allure Plugin (for post.allure), env ALLURE_HISTORY_URL for trend charts (e.g. https://user.github.io/repo).

pipeline {
    agent any

    options {
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

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
                expression { return isUnix() }
            }
            steps {
                sh '''
                    if ! command -v google-chrome &> /dev/null; then
                        echo "Installing Chrome..."
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
                sh 'mkdir -p target/allure-report'
                script {
                    // Optional: set ALLURE_HISTORY_URL in Jenkins (e.g. https://user.github.io/repo) to download trends
                    if (env.ALLURE_HISTORY_URL?.trim()) {
                        sh """
                            wget -q -r -np -nH -e robots=off -P target/allure-report-dl "${env.ALLURE_HISTORY_URL}/history/" || true
                            HIST_DIR=\$(find target/allure-report-dl -type d -name history 2>/dev/null | head -1)
                            if [ -n "\$HIST_DIR" ] && [ -d "\$HIST_DIR" ]; then mv "\$HIST_DIR" target/allure-report/; fi
                            rm -rf target/allure-report-dl 2>/dev/null || true
                        """
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    def runSmoke = env.BRANCH_NAME != 'main' && env.BRANCH_NAME != 'master'
                    if (runSmoke) {
                        sh 'mvn test -Pcucumber -B -Dheadless=true -Dcucumber.filter.tags=@smoke'
                    } else {
                        sh 'mvn test -Pcucumber -B -Dheadless=true'
                    }
                }
            }
        }

        stage('Generate Allure report') {
            when {
                always()
            }
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
            archiveArtifacts artifacts: 'target/surefire-reports/**,target/cucumber-reports.html,target/cucumber-report.json,target/allure-report/**',
                allowEmptyArchive: true,
                fingerprint: true
        }
        failure {
            echo 'Pipeline or test stage failed.'
        }
    }
}
