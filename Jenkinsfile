pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK17'
    }

    environment {
        GIT_BRANCH = "${env.BRANCH_NAME ?: env.GIT_BRANCH ?: 'develop'}"
        DEPLOY_ENV = "${env.GIT_BRANCH == 'main' ? 'production' : env.GIT_BRANCH == 'develop' ? 'staging' : 'testing'}"

        APP_NAME = 'mtd-api'
        VERSION = getVersionFromBranch("${env.GIT_BRANCH}")
        DOCKER_IMAGE_TAG = "${APP_NAME}:${VERSION}-${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                bat 'git branch --show-current'
                echo "Deploying to ${env.DEPLOY_ENV} environment from branch ${env.GIT_BRANCH}"
            }
        }

        stage('Build and Test') {
            steps {
                bat 'mvn clean package'

                // Verificar que el JAR fue creado correctamente
                bat 'dir target\\*.jar'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                    junit '**/target/surefire-reports/TEST-*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                // Asegurarse de que el archivo JAR exista antes de construir la imagen
                bat 'dir target\\*.jar || (echo "ERROR: JAR file not found" && exit 1)'

                bat "docker build -t ${DOCKER_IMAGE_TAG} ."
                bat "docker tag ${DOCKER_IMAGE_TAG} ${APP_NAME}:latest"
            }
        }

        stage('Deploy') {
            when {
                anyOf {
                    branch 'develop'
                    branch 'main'
                }
            }

            steps {
                script {
                    // Configura el entorno basado en la rama
                    def envFile = "${env.DEPLOY_ENV}.env"

                    bat """
                        set CLOUDFLARE_R2_ACCESS_KEY=${env.CLOUDFLARE_R2_ACCESS_KEY}
                        set CLOUDFLARE_R2_SECRET_KEY=${env.CLOUDFLARE_R2_SECRET_KEY}
                        set CLOUDFLARE_R2_BUCKET_NAME=${env.CLOUDFLARE_R2_BUCKET_NAME}
                        set CLOUDFLARE_R2_ENDPOINT=${env.CLOUDFLARE_R2_ENDPOINT}

                        # Usa el archivo docker-compose específico del entorno si existe
                        if exist "docker-compose.${env.DEPLOY_ENV}.yml" (
                            docker-compose -f docker-compose.${env.DEPLOY_ENV}.yml down
                            docker-compose -f docker-compose.${env.DEPLOY_ENV}.yml up -d
                        ) else (
                            docker-compose down
                            docker-compose up -d
                        )
                    """
                }
            }
        }
    }

    post {
        always {
            node {  // Sin etiqueta específica
                cleanWs()
            }
        }
        success {
            echo "Pipeline completed successfully for ${env.GIT_BRANCH} branch!"
        }
        failure {
            echo "Pipeline failed for ${env.GIT_BRANCH} branch"
        }
    }
}

// Función para obtener la versión basada en la rama
def getVersionFromBranch(branch) {
    if (branch == 'main') {
        return "v1.0"
    } else if (branch == 'develop') {
        return "v1.0-SNAPSHOT"
    } else if (branch.startsWith('feature/')) {
        return "v1.0-${branch.split('/')[1]}"
    } else {
        return "v1.0-BETA"
    }
}
