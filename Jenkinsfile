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

        stage('Spotless') {
            steps {
                sh 'mvn -B -pl p2p-common spotless:check'
            }
        }

        stage('Checkstyle') {
            steps {
                sh 'mvn -B -pl p2p-common checkstyle:check'
            }
        }

        stage('SpotBugs') {
            steps {
                sh 'mvn -B -pl p2p-common compile spotbugs:check'
            }
        }

        stage('Unit Test & Build') {
            steps {
                sh 'mvn -B -pl p2p-common -am clean verify'
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
