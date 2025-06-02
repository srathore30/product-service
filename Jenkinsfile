pipeline {
    agent any

    tools {
        maven 'maven-3.9.6'     // Make sure this Maven version is configured in Jenkins
        jdk 'Java 17'
    }

    environment {
        VPS_HOST = '195.35.22.253'
        VPS_USER = 'root'
        CREDENTIALS_ID = 'vps-ssh-credentials-id-credentialsId'
        REMOTE_SERVICE_NAME = 'product-service'
        JAR_NAME = 'prouduct-0.0.1-SNAPSHOT.jar'
        REMOTE_PATH = "/home/ubuntu/sfa-service/product-service"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm: [$class: 'GitSCM',
                    branches: [[name: '*/deployjenkins']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/srathore30/product-service.git',
                        credentialsId: 'github-token-pipeline'
                    ]]
                ]
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Add Host Key') {
            steps {
                sh "mkdir -p ~/.ssh"
                sh "ssh-keyscan -H ${env.VPS_HOST} >> ~/.ssh/known_hosts"
            }
        }


        stage('Deploy to VPS') {
            steps {
                sshagent([env.CREDENTIALS_ID]) {
                    sh """
                        echo "Copying JAR..."
                        scp target/${env.JAR_NAME} ${env.VPS_USER}@${env.VPS_HOST}:${env.REMOTE_PATH}/

                        echo "Copying startup script..."
                        scp ./productStartUp.sh ${env.VPS_USER}@${env.VPS_HOST}:${env.REMOTE_PATH}/

                        echo "Running deployment script on server..."
                        ssh ${env.VPS_USER}@${env.VPS_HOST} '
                            set -e
                            cd ${REMOTE_PATH}
                            echo "Removing old nohup.out if exists..."
                            rm -f nohup.out

                            echo "Starting service..."
                            chmod +x productStartUp.sh
                            ./productStartUp.sh

                            echo "Deployment done!"
                        '
                    """
                }
            }
        }
    }
}
