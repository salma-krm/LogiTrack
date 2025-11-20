pipeline {
  agent any
  tools {
      // Hna fin katgol l Jenkins ykhdem b JDK-17
      jdk 'JDK-17' // <-- Smiya li derti f Global Tool Configuration
      maven 'maven' // Mzyan t7aded tal version d Maven
  }

  environment {
    MVN_CMD = "mvnw.cmd"
    MAVEN_OPTS = "-Xmx1g"
  }

  options {
    timestamps()
    ansiColor('xterm')
    timeout(time: 60, unit: 'MINUTES')
    buildDiscarder(logRotator(numToKeepStr: '25'))
  }

  stages {

    stage('Checkout') {
      steps {
        checkout scm
        // Windows diagnostics only
        bat 'dir'
      }
    }

    stage('Build (compile)') {
      steps {
        bat "${MVN_CMD} -B -DskipTests=true clean package"
      }
    }

    stage('Unit Tests') {
      steps {
        bat "${MVN_CMD} -B test"
      }
    }

    stage('JaCoCo Report') {
      steps {
        bat "${MVN_CMD} -B jacoco:report"
      }
    }

     stage('SonarQube Analysis') {
          steps {
            script {
              // ⚠️ Remplace 'SonarQubeServer' par le nom exact configuré dans Jenkins
              withSonarQubeEnv('logiTrack') {
                 bat "${MVN_CMD} sonar:sonar -Dsonar.exclusions=**/dto/**,**/mapper/**"
              }


            }
          }
        }

    stage('Package') {
      steps {
        bat "${MVN_CMD} -B -DskipTests=true package"
      }
    }
  }

  post {
    always {
      junit allowEmptyResults: true, testResults: 'target/surefire-reports/.xml'
      archiveArtifacts artifacts: 'target/.jar, target/site/jacoco/', allowEmptyArchive: true
      cleanWs()
    }

    success {
      echo "Build succeeded: ${env.BUILD_URL}"
    }

    failure {
      echo "Build failed: ${env.BUILD_URL}"
    }
  }
}