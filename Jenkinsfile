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
        JAR_NAME = 'prouduct-0.0.1-SNAPSHOT.jar'
        REMOTE_PATH = "/home/ubuntu/sfa-service/product-service"
        TEMP_PATH = "/home/ubuntu/sfa-service/product-service/temp"
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
                    echo "Generating startup script..."

                    cat << 'EOF' > ${env.STARTUP_SCRIPT}
#!/bin/bash

echo "[INFO] Checking for running service on port 9093..."
PID=\$(sudo lsof -t -i:9093)

if [ -n "\$PID" ]; then
    echo "[INFO] Found running process with PID: \$PID. Killing it..."
    sudo kill -9 \$PID
    echo "[INFO] Process killed."
else
    echo "[INFO] No process running on port 9093."
fi

echo "[INFO] Starting new JAR..."
nohup java -jar ${env.JAR_NAME} > nohup.out 2>&1 &
echo "[INFO] New service started successfully!"

EOF
                    chmod +x ${env.STARTUP_SCRIPT}
                """
            }
        }

        stage('Deploy to VPS') {
            steps {
                sshagent([env.CREDENTIALS_ID]) {
                    sh """
                        echo "[INFO] Creating temporary directory on VPS..."
                        ssh ${env.VPS_USER}@${env.VPS_HOST} 'mkdir -p ${TEMP_PATH}'

                        echo "[INFO] Copying new JAR to temporary folder..."
                        scp target/${env.JAR_NAME} ${env.VPS_USER}@${env.VPS_HOST}:${TEMP_PATH}/

                        echo "[INFO] Copying startup script to VPS..."
                        scp ${env.STARTUP_SCRIPT} ${env.VPS_USER}@${env.VPS_HOST}:${REMOTE_PATH}/

                        echo "[INFO] Executing deployment on VPS..."
                        ssh ${env.VPS_USER}@${env.VPS_HOST} '
                            set -e
                            cd ${REMOTE_PATH}

                            echo "[INFO] Removing old JAR..."
                            rm -f *.jar

                            echo "[INFO] Moving new JAR from temp to service directory..."
                            mv ${TEMP_PATH}/*.jar ${REMOTE_PATH}/

                            echo "[INFO] Cleaning up temporary folder..."
                            rm -rf ${TEMP_PATH}

                            echo "[INFO] Executing startup script..."
                            chmod +x productStartUp.sh
                            ./productStartUp.sh

                            echo "[INFO] Deployment completed."
                        '
                    """
                }
            }
        }
    }
}