// pipeline {
//     agent any
//
//     tools {
//         maven 'maven-3.9.6'
//         jdk 'Java 17'
//     }
//
//     environment {
//         VPS_HOST = '195.35.22.253'
//         VPS_USER = 'root'
//         CREDENTIALS_ID = 'vps-ssh-credentials-id-credentialsId'
//         REMOTE_SERVICE_NAME = 'product-service'
//         JAR_NAME = 'prouduct-0.0.1-SNAPSHOT.jar'
//         REMOTE_PATH = "/home/ubuntu/sfa-service/product-service"
//         STARTUP_SCRIPT = "/tmp/productStartUp.sh"
//     }
//
//     stages {
//         stage('Checkout') {
//             steps {
//                 checkout scm: [$class: 'GitSCM',
//                     branches: [[name: '*/deployjenkins']],
//                     userRemoteConfigs: [[
//                         url: 'https://github.com/srathore30/product-service.git',
//                         credentialsId: 'github-token-pipeline'
//                     ]]
//                 ]
//             }
//         }
//
//         stage('Build') {
//             steps {
//                 sh 'mvn clean package -DskipTests'
//             }
//         }
//
//         stage('Test') {
//             steps {
//                 sh 'mvn test'
//             }
//         }
//
//         stage('Add Host Key') {
//             steps {
//                 sh '''
//                     mkdir -p ~/.ssh
//                     ssh-keyscan -H $VPS_HOST >> ~/.ssh/known_hosts
//                 '''
//             }
//         }
//
//         stage('Prepare Start Script') {
//             steps {
//                 sh """
//                     cat << 'EOF' > ${env.STARTUP_SCRIPT}
// #!/bin/bash
// echo "Stopping old service..."
// pkill -f ${env.JAR_NAME}
//
// echo "Starting new service..."
// nohup java -jar ${env.JAR_NAME} > output.log 2>&1 &
// echo "Service started successfully!"
// EOF
//                     chmod +x ${env.STARTUP_SCRIPT}
//                 """
//             }
//         }
//
//         stage('Deploy to VPS') {
//             steps {
//                 sshagent([env.CREDENTIALS_ID]) {
//                     sh """
//                         echo "Copying JAR..."
//                         scp target/${env.JAR_NAME} ${env.VPS_USER}@${env.VPS_HOST}:${env.REMOTE_PATH}/
//
//                         echo "Copying startup script..."
//                         scp ${env.STARTUP_SCRIPT} ${env.VPS_USER}@${env.VPS_HOST}:${env.REMOTE_PATH}/
//
//                         echo "Running deployment script on server..."
//                         ssh ${env.VPS_USER}@${env.VPS_HOST} '
//                             set -e
//                             cd ${REMOTE_PATH}
//                             chmod +x productStartUp.sh
//                             ./productStartUp.sh
//                         '
//                     """
//                 }
//             }
//         }
//     }
// }

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
        JAR_NAME = 'prouduct-0.0.1-SNAPSHOT.jar'  // spelling verify करें!
        REMOTE_PATH = "/home/ubuntu/sfa-service/product-service"
        STARTUP_SCRIPT = "/tmp/productStartUp.sh"
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Checking out code from Git..."
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
                echo "Building the project with Maven..."
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo "Running tests..."
                sh 'mvn test'
            }
        }

        stage('Add Host Key') {
            steps {
                echo "Adding VPS host key to known_hosts"
                sh '''
                    mkdir -p ~/.ssh
                    ssh-keyscan -H $VPS_HOST >> ~/.ssh/known_hosts
                '''
            }
        }

        stage('Prepare Start Script') {
            steps {
                echo "Preparing startup script..."
                sh """
                    cat << 'EOF' > ${env.STARTUP_SCRIPT}
#!/bin/bash
echo "Stopping old service..."
pkill -f ${env.JAR_NAME} || echo "No old process found"

echo "Sleeping 2 seconds to free port..."
sleep 2

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
                echo "Deploying artifacts to VPS..."
                sshagent([env.CREDENTIALS_ID]) {
                    sh """
                        set -x
                        echo "Copying JAR file..."
                        scp target/${env.JAR_NAME} ${env.VPS_USER}@${env.VPS_HOST}:${env.REMOTE_PATH}/

                        echo "Copying startup script..."
                        scp ${env.STARTUP_SCRIPT} ${env.VPS_USER}@${env.VPS_HOST}:${env.REMOTE_PATH}/

                        echo "Running deployment script remotely..."
                        ssh ${env.VPS_USER}@${env.VPS_HOST} '
                            set -xe
                            cd ${REMOTE_PATH}
                            echo "Listing files in deployment directory:"
                            ls -l

                            echo "Killing old Java process if exists..."
                            pkill -f ${env.JAR_NAME} || echo "No old process found"

                            echo "Waiting 2 seconds..."
                            sleep 2

                            echo "Making startup script executable..."
                            chmod +x productStartUp.sh

                            echo "Executing startup script..."
                            ./productStartUp.sh

                            echo "Checking running Java process..."
                            ps -ef | grep java | grep ${env.JAR_NAME} || echo "Process not found"
                        '
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Deployment succeeded!'
        }
        failure {
            echo 'Deployment failed. Please check the logs.'
        }
    }
}
