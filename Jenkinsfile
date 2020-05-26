properties([pipelineTriggers([githubPush()])])

pipeline {
    agent { label 'gungnir' }

    stages {

        stage('Package artifact') {
            steps {
                sh "mvn clean package --update-snapshots"
            }
        }

        stage('Build and publish docker images') {
            steps {
                script {
                    sh "mvn docker:build -DdockerImageTags=latest"
                }
            }
        }

        stage('Deploy'){
            environment {
                STORAGE_SERVER_SECRET=credentials('binary-storage-secret')
            }
            steps {
                script {
                    sh 'docker-compose up -d'
                }
            }
        }
    }

    tools {
        maven 'Maven 3.5.0'
        jdk 'openjdk-1.8.0'
    }
}
