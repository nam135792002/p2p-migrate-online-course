
pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Spotless - Full Project') {
            steps {
                sh 'mvn -B -f pom.xml spotless:check'
            }
        }

        stage('Checkstyle - Full Project') {
            steps {
                sh '''
                    mvn -B -f pom.xml \
                        checkstyle:check \
                        -Dcheckstyle.consoleOutput=true
                '''
            }

            post {
                always {
                    archiveArtifacts(
                        artifacts: '**/target/checkstyle-result.xml',
                        allowEmptyArchive: true
                    )
                }
            }
        }

        stage('SpotBugs - Full Project') {
            steps {
                sh 'mvn -B -f pom.xml compile spotbugs:check'
            }
        }

        stage('Unit Test & Build - Full Project') {
            steps {
                sh 'mvn -B -f pom.xml clean verify'
            }
        }

        stage('Semgrep CE - Full Project') {
            steps {
                sh '''
                    mkdir -p target

                    semgrep scan \
                        --config p/java \
                        --severity ERROR \
                        --error \
                        --metrics=off \
                        --json \
                        --output target/semgrep-report.json \
                        .
                '''
            }

            post {
                always {
                    archiveArtifacts(
                        artifacts: 'target/semgrep-report.json',
                        allowEmptyArchive: true
                    )
                }
            }
        }
    }

    post {
        success {
            echo 'CI PASSED - All quality gates passed.'
        }

        failure {
            echo 'CI FAILED - Pull Request must not be merged.'
        }

        always {
            junit allowEmptyResults: true,
                  testResults: '**/target/surefire-reports/*.xml'
        }
    }
}
