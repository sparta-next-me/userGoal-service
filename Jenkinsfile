pipeline {
    agent any

    environment {
        APP_NAME        = "usergoal-service"
        NAMESPACE       = "next-me"
        REGISTRY        = "ghcr.io"
        GH_OWNER        = "sparta-next-me"
        IMAGE_REPO      = "usergoal-service"
        FULL_IMAGE      = "${REGISTRY}/${GH_OWNER}/${IMAGE_REPO}:latest"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build with Gradle') {
            steps {
                // usergoal-env 파일을 사용하여 빌드 수행
                withCredentials([file(credentialsId: 'usergoal-env', variable: 'ENV_FILE')]) {
                    sh '''
                      set -a
                      . "$ENV_FILE"
                      set +a
                      chmod +x ./gradlew
                      ./gradlew clean bootJar --no-daemon
                    '''
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'ghcr-credential', usernameVariable: 'USER', passwordVariable: 'TOKEN')]) {
                    sh """
                      docker build -t ${FULL_IMAGE} .
                      echo "${TOKEN}" | docker login ${REGISTRY} -u "${USER}" --password-stdin
                      docker push ${FULL_IMAGE}
                    """
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([
                    file(credentialsId: 'k3s-kubeconfig', variable: 'KUBECONFIG_FILE'),
                    file(credentialsId: 'usergoal-env', variable: 'ENV_FILE')
                ]) {
                    sh '''
                      export KUBECONFIG=${KUBECONFIG_FILE}

                      # Secret 업데이트
                      kubectl delete secret usergoal-env -n ${NAMESPACE} --ignore-not-found
                      kubectl create secret generic usergoal-env --from-env-file=${ENV_FILE} -n ${NAMESPACE}

                      # YAML 적용
                      kubectl apply -f usergoal-service.yaml -n ${NAMESPACE}

                      # 배포 상태 모니터링
                      kubectl rollout status deployment/usergoal-service -n ${NAMESPACE}
                    '''
                }
            }
        }
    }

    post {
        always {
            echo "Cleaning up Docker resources..."
            sh "docker rmi ${FULL_IMAGE} || true"
            // 사용하지 않는 매달린 이미지/컨테이너 정리 (용량 확보)
            sh "docker system prune -f"
        }
    }
}