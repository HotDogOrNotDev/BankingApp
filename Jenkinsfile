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

        stage('Build Docker Image') {
            steps {
                script {
                    try {
                        updateGitlabCommitStatus name: 'docker', state: 'running'
                        def dockerImage = docker.build("dao-layer:${BUILD_NUMBER}")
                        dockerImage.tag('latest')
                        updateGitlabCommitStatus name: 'docker', state: 'success'
                    } catch (Exception e) {
                        updateGitlabCommitStatus name: 'docker', state: 'failed'
                        error "Error building Docker image: ${e.message}"
                    }
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                script {
                    try {
                        updateGitlabCommitStatus name: 'deploy', state: 'running'
                        sh '''
                            docker stop dao-layer || true
                            docker rm dao-layer || true
                            docker run -d --name dao-layer \
                                -p 9002:9002 \
                                --restart unless-stopped \
                                dao-layer:latest
                        '''
                        updateGitlabCommitStatus name: 'deploy', state: 'success'
                    } catch (Exception e) {
                        updateGitlabCommitStatus name: 'deploy', state: 'failed'
                        error "Error running container: ${e.message}"
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
                docker stop dao-layer || true
                docker rm dao-layer || true
            '''
        }
        always {
            sh '''
                docker stop dao-layer || true
                docker rm dao-layer || true

                docker image prune -f
                docker images -q dao-layer | grep -v "$(docker inspect --format='{{range .RepoTags}} {{.}} {{end}}' dao-layer | grep latest)" | xargs -r docker rmi -f || true
            '''
        }
    }
}