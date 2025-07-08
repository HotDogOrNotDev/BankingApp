pipeline {
    agent any

    options {
        gitLabConnection('gitlab')
        timeout(time: 1, unit: 'HOURS')
    }

    triggers {
        gitlab(triggerOnPush: true, triggerOnMergeRequest: true, branchFilterType: 'All')
    }

    stages {
        stage('Build Maven Project') {
            steps {
                updateGitlabCommitStatus name: 'pipeline', state: 'running'
                updateGitlabCommitStatus name: 'build', state: 'running'
                sh 'chmod +x mvnw'
                sh './mvnw clean install -DskipTests'
                updateGitlabCommitStatus name: 'build', state: 'success'
            }
        }

        stage('Build Docker Image main-app') {
            steps {
                script {
                    try {
                        updateGitlabCommitStatus name: 'docker-main-app', state: 'running'
                        def dockerImageMain = docker.build("main-app:${BUILD_NUMBER}", "main-app/")
                        dockerImageMain.tag('latest')
                        updateGitlabCommitStatus name: 'docker-main-app', state: 'success'
                    } catch (Exception e) {
                        updateGitlabCommitStatus name: 'docker-main-app', state: 'failed'
                        error "Error building main-app Docker image: ${e.message}"
                    }
                }
            }
        }

        stage('Build Docker Image currency-module') {
            steps {
                script {
                    try {
                        updateGitlabCommitStatus name: 'docker-currency-module', state: 'running'
                        def dockerImageCurrency = docker.build("currency-module:${BUILD_NUMBER}", "currency-module/")
                        dockerImageCurrency.tag('latest')
                        updateGitlabCommitStatus name: 'docker-currency-module', state: 'success'
                    } catch (Exception e) {
                        updateGitlabCommitStatus name: 'docker-currency-module', state: 'failed'
                        error "Error building currency-module Docker image: ${e.message}"
                    }
                }
            }
        }

        stage('Run Docker Container main-app') {
            steps {
                script {
                    try {
                        updateGitlabCommitStatus name: 'deploy-main-app', state: 'running'
                        sh '''
                            docker stop main-app || true
                            docker rm main-app || true
                            docker run -d --name main-app \
                                -p 9002:9002 \
                                --restart unless-stopped \
                                main-app:latest
                        '''
                        updateGitlabCommitStatus name: 'deploy-main-app', state: 'success'
                    } catch (Exception e) {
                        updateGitlabCommitStatus name: 'deploy-main-app', state: 'failed'
                        error "Error running main-app container: ${e.message}"
                    }
                }
            }
        }

        stage('Run Docker Container currency-module') {
            steps {
                script {
                    try {
                        updateGitlabCommitStatus name: 'deploy-currency-module', state: 'running'
                        sh '''
                            docker stop currency-module || true
                            docker rm currency-module || true
                            docker run -d --name currency-module \
                                -p 8081:8081 \
                                --restart unless-stopped \
                                currency-module:latest
                        '''
                        updateGitlabCommitStatus name: 'deploy-currency-module', state: 'success'
                    } catch (Exception e) {
                        updateGitlabCommitStatus name: 'deploy-currency-module', state: 'failed'
                        error "Error running currency-module container: ${e.message}"
                    }
                }
            }
        }
    }

    post {
        success {
            updateGitlabCommitStatus name: 'pipeline', state: 'success'
        }
        failure {
            updateGitlabCommitStatus name: 'pipeline', state: 'failed'
            sh '''
                docker stop main-app || true
                docker rm main-app || true
                docker stop currency-module || true
                docker rm currency-module || true
            '''
        }
        always {
            sh '''
                docker stop main-app || true
                docker rm main-app || true
                docker stop currency-module || true
                docker rm currency-module || true

                docker image prune -f
                docker images -q main-app | grep -v "$(docker inspect --format='{{range .RepoTags}} {{.}} {{end}}' main-app | grep latest)" | xargs -r docker rmi -f || true
                docker images -q currency-module | grep -v "$(docker inspect --format='{{range .RepoTags}} {{.}} {{end}}' currency-module | grep latest)" | xargs -r docker rmi -f || true
            '''
        }
    }
}