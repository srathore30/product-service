pipeline {
    agent any

    tools {
        maven 'maven-3.9.6'
        jdk 'Java 17'
    }

    environment {
        VPS_HOST = '195.35.22.253'
        VPS_USER = 'root'
        CREDENTIALS_ID = 'vps-ssh-credentials-id-credentialsId'
        REMOTE_SERVICE_NAME = 'product-service'
        JAR_NAME = 'prouduct-0.0.1-SNAPSHOT.jar'
        REMOTE_PATH = "/home/ubuntu/sfa-service/product-service"
        STARTUP_SCRIPT = "/tmp/productStartUp.sh"
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
                sh '''
                    mkdir -p ~/.ssh
                    ssh-keyscan -H $VPS_HOST >> ~/.ssh/known_hosts
                '''
            }
        }

        stage('Prepare Start Script') {
            steps {
                sh """
                    cat << 'EOF' > ${env.STARTUP_SCRIPT}
#!/bin/bash
echo "Stopping old service..."
pkill -f ${env.JAR_NAME}

echo "Starting new service..."
nohup java -jar ${env.JAR_NAME} > output.log 2>&1 &
echo "Service started successfully!"
EOF
                    chmod +x ${env.STARTUP_SCRIPT}
                """
            }
        }

        stage('Deploy to VPS') {
            steps {
                sshagent([env.CREDENTIALS_ID]) {
                    sh """
                                    echo "Copying JAR..."
                                    scp target/${env.JAR_NAME} ${env.VPS_USER}@${env.VPS_HOST}:${env.REMOTE_PATH}/

                                    echo "Copying startup script..."
                                    scp ${env.STARTUP_SCRIPT} ${env.VPS_USER}@${env.VPS_HOST}:${env.REMOTE_PATH}/

                                    echo "Running deployment script on server..."
                                    ssh ${env.VPS_USER}@${env.VPS_HOST} "
                                        set -e
                                        cd ${REMOTE_PATH}
                                        echo 'Current directory: ' \$(pwd)
                                        echo 'Files here:'
                                        ls -l

                                        # Kill old process
                                        pkill -f ${JAR_NAME} || echo 'No old process found'

                                        # Give some time to release port
                                        sleep 2

                                        # Start new process
                                        chmod +x productStartUp.sh
                                        ./productStartUp.sh

                                        # Check process list
                                        ps -ef | grep java | grep ${JAR_NAME}
                                    "
                                """
                }
            }
        }
    }
}
