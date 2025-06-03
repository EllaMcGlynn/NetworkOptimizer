pipeline {
    agent any

    tools {
        maven "Maven 3.9.9"
    }

    environment {
        APP_NAME = "AURA"
        TAG = ""
    }

    stages{

        stage("Build") {
            steps {
                sh "mvn install -DskipTests"
            }
        }

        stage("Test") {
            steps {
                sh "mvn test"
            }
        }

        stage('SonarQube Analysis') {
            withSonarQubeEnv("SonarQube") {
              sh "mvn clean verify sonar:sonar -Dsonar.projectKey=AURA -Dsonar.projectName='AURA'"
            }
      }

        stage("Deploy") {
            steps {
                sh "docker compose up --build"
            }
        }
    }
}
