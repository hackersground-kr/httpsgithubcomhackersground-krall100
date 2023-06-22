# `올백` - `치매환자 보호 서비스`

해커그라운드 해커톤에 참여하는 `올백` 팀의 `치매환자 보호 서비스`입니다.

## 참고 문서

> 아래 두 링크는 해커톤에서 앱을 개발하면서 참고할 만한 문서들입니다. 이 문서들에서 언급한 서비스 이외에도 더 많은 서비스들이 PaaS, SaaS, 서버리스 형태로 제공되니 참고하세요.

- [순한맛](./REFERENCES_BASIC.md)
- [매운맛](./REFERENCES_ADVANCED.md)

## 제품/서비스 소개

<!-- 아래 링크는 지우지 마세요 -->
[제품/서비스 소개 보기](TOPIC.md)
<!-- 위 링크는 지우지 마세요 -->

## 오픈 소스 라이센스

<!-- 아래 링크는 지우지 마세요 -->
[오픈소스 라이센스 보기](./LICENSE)
<!-- 위 링크는 지우지 마세요 -->

## 설치 방법

> **아래 제공하는 설치 방법을 통해 심사위원단이 여러분의 제품/서비스를 실제 Microsoft 애저 클라우드에 배포하고 설치할 수 있어야 합니다. 만약 아래 설치 방법대로 따라해서 배포 및 설치가 되지 않을 경우 본선에 진출할 수 없습니다.**

### 사전 준비 사항

> **여러분의 제품/서비스를 Microsoft 애저 클라우드에 배포하기 위해 사전에 필요한 준비 사항들을 적어주세요.**
- GitHub Account
- Azure Free Account
- Visual Studio Code
- GitHub CLI
- Azure CLI
- Azure Developer CLI

## 시작하기

> **여러분의 제품/서비스를 Microsoft 애저 클라우드에 배포하기 위한 절차를 구체적으로 나열해 주세요.**


### 1. Azure 포탈에서 리소스 그룹 생성
- https://portal.azure.com 에서 리소스 그룹을 생성합니다.
- 리소스 그룹 이름은 rg-hg(자신이 원하는 숫자조합)으로 하며, 리소스 그룹의 위치는 Korea Central로 합니다.
- 앞으로 작성하는 숫자조합은 모두 위와 같은 숫자조합이어야 합니다.

### 2. Repository fork
- 현재 Repository를 fork한 다음, codespace를 생성해주세요.
- Repository에서 Actions 탭에서 GitHub Actions를 Enable하도록 설정해주세요.

### 3. App service & App service plan 생성
- 생성한 리소스 그룹 안에 App service와 App service plan을 생성해주세요.
  
#### - `App Service plan` 생성    
  ![image](https://github.com/hackersground-kr/httpsgithubcomhackersground-krall100/assets/105070397/df12aa4d-948a-4bc9-9e6c-0bd3626b622b)

   - 이름 : asplan-hg(생성한 숫자조합)
   - 운영체제 : 리눅스    
   - 지역 : Korea Central    
   - 가격 플랜 : B1    

#### - `App Service` 생성    
  ![image](https://github.com/hackersground-kr/httpsgithubcomhackersground-krall100/assets/84391428/7719ac1d-619f-4f18-bfff-5b5bea877ec2)
   - 이름 : appsvc-hg(생성한 숫자조합)    
   - 런타임 스택 : .NET7    
   - 운영체제 : 리눅스    
   - 지역 : Central Korea    
   - 가격 플랜 : B1    
   - 리눅스 플랜 : 기본 default로 해주세요    


### 4. Secrete Key 설정하기
- codespace의 bash 터미널에서 아래 명령어를 입력합니다.

    ```
    azd auth login —use-device-code
    ```

    터미널에 보여지는 코드를 복사해두고,
    터미널에서 enter를 누릅니다.
    열린 브라우저에 복사한 코드를 입력하고 로그인해주세요.

    ![image](https://github.com/hackersground-kr/httpsgithubcomhackersground-krall100/assets/105070397/fa56beb5-6665-49d2-890a-aefcd8a294f7)

    

- bash 터미널에 아래 명령어를 차례로 입력합니다.

    ```
    AZURE_ENV_NAME="hg{위에서 만든 숫자조합}"
    echo $AZURE_ENV_NAME
    ```


### 5. 파라미터 설정
- bash 터미널 아래 명령어를 입력해주세요.
```
AZURE_APPSERVICE_NAME={생성한 앱 서비스 이름}
AZURE_RG_NAME={생성한 리소스 그룹 이름}
```


### 6. Secretes 생성

- 터미널에서 아래 명령어를 통해 애저에 로그인합니다.

    ```
    az login --use-device-code
    ```

    출력되는 결과에서 링크에 마우스를 대고 ctrl+클릭을 합니다. 
    또한, 결과에 보여지는 코드를 복사하여 웹사이트에서 로그인하는데 사용합니다.

- 터미널에서 아래 명령어를 입력하여 현재 구독을 확인합니다.

    ```
    az account show
    ```

- 아래 명령어를 통해 애저 로그인 키를 생성합니다. 이 때 이름의 `hg{{숫자}}`는 앞서 생성한 `AZURE_ENV_NAME`의 값입니다. `echo $AZURE_ENV_NAME` 명령어로 확인할 수 있습니다.

    ```
    subscriptionId=$(az account show --query "id" -o tsv)
    az ad sp create-for-rbac \
        --name "spn-hg{{숫자}}" --role contributor \
        --scopes /subscriptions/$subscriptionId \
        --sdk-auth
    ```

   생성된 키는 json 개체로, 아래와 같은 형식입니다.

    ```
    {
      "clientId": "9814fa03-26b8-4ce0-9021-121d16cc929f",
      "clientSecret": "uOR^Pt*Tv7iMqtzrQe1Zq5Djlf*K4P8QzALlNfw#",
      "subscriptionId": "a348f9f2-dc63-4083-b19b-4f235e0b5175",
      "tenantId": "4e18dd75-d9e5-4afc-ad7e-ac436d98e812",
      "activeDirectoryEndpointUrl": "https://login.microsoftonline.com",
      "resourceManagerEndpointUrl": "https://management.azure.com/",
      "activeDirectoryGraphResourceId": "https://graph.windows.net/",
      "sqlManagementEndpointUrl": "https://management.core.windows.net:8443/",
      "galleryEndpointUrl": "https://gallery.azure.com/",
      "managementEndpointUrl": "https://management.core.windows.net/"
    }
    ```

- fork한 GitHub repository에서 Settings 탭의 Secrets and variables 섹션에서 Secrets를 생성합니다.
![image](https://github.com/hackersground-kr/httpsgithubcomhackersground-krall100/assets/105070397/cf6cb91e-6038-4427-b655-49553368eaf6)

  
   (1) `AZURE_CREDENTIALS`  Secrets 생성
     - Name : AZURE_CREDENTIALS
     - 내용 : 앞서 생성한 json 개체


   (2) `AZURE_ENV_NAME` Secrets 생성    
     - Name : AZURE_ENV_NAME    
   - 내용 : hg(랜덤숫자) (echo $AZURE_ENV_NAME을 bash에 치면 확인 가능)
  
   (3) `AZURE_WEBAPP_NAME` Variables 생성
     - Name : AZURE_WEBAPP_NAME
     - 내용 : {앱서비스 이름}


### 7. 배포

- README.md 파일에 뭐든지 주석을 남겨주세요.
- 아래 명령어를 통해 코드를 푸시하고 GitHub 액션 워크플로우가 작동하는 것을 확인합니다.


```
git add .
git commit -m "Add GitHub Actions workflow"
git push origin
```
