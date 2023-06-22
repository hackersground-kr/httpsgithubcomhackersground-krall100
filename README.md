# `{{ 올백 }}` - `{{ 치매환자 보호 서비스 }}`

해커그라운드 해커톤에 참여하는 `{{ 올백 }}` 팀의 `{{ 치매환자 보호 서비스 }}`입니다.

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
1. Azure로 리소스 그룹을 만들어주세요.
   - 리소스 그룹 이름은 rg-hg(랜덤숫자조합)으로 하며, 리소스 그룹의 위치는 Korea Central로 합니다.
![image](https://github.com/hackersground-kr/httpsgithubcomhackersground-krall100/assets/84391428/7719ac1d-619f-4f18-bfff-5b5bea877ec2)

2. 저희 GitHub에서 fork를 해주세요
   
3. app service & app service plan 만들기
   - App Service plan (리소스 그룹 밖에서 검색시 App Service 요금제) 부터 만들어주세요
     * 리소스 그룹 밖에서 검색해주세요
     이름 : asplan-hg(랜덤숫자)
     운영체제 : 리눅스
     지역 : Central Korea
     가격 플랜 : B1
   - App Service (검색시 웹 앱)
     이름 : appsvc-hg(랜덤숫자)
     운영체제 : 리눅스
     지역 : Central Korea
     가격 플랜 : B1
     리눅스 플랜 : 기본 default로 해주세요
4. Secrete Key 설정하기
- 터미널에서 아래 명령어를 입력합니다.

    ```bash
    azd auth login
    ```

   > 새 웹 브라우저 탭이 나타나면서 404 에러가 보인다면 주소창의 `http://localhost...`로 시작하는 주소를 복사해서 새 터미널 창에 `curl` 명령어와 함께 붙여넣습니다.
   > 이 때 새 터미널 창을 bash 터미널로 열어서 잘 실행이 안 된다면, zsh 터미널로 열어서 해 보세요.

- 아래 명령어를 차례로 입력합니다.

    ```bash
    # Set AZURE_ENV_NAME
    AZURE_ENV_NAME="hg$RANDOM"

    # Get AZURE_ENV_NAME
    echo $AZURE_ENV_NAME
    ```

- 아래 명령어를 입력해서 Azure Dev CLI 구성을 시작합니다.

    ```bash
    azd init -e $AZURE_ENV_NAME
    ```

   아래와 같은 프롬프트가 몇 개 나오는데 계속해서 `y`를 입력합니다.

   - **The current directory is not empty. Would you like to initialize a project here in '/workspaces/{{REPOSITORY_NAME}}'? (y/N)** 👈 `y` 입력
   - **Select a project template:  [Use arrows to move, type to filter]** 👈 `Starter - Bicep` 선택
   - **What would you like to do with these files?  [Use arrows to move, type to filter]** 👈 `Keep my existing files unchanged` 선택
5. key github에 넣어주기
- 보류
6. secrete key codespace에 반영
- main_parameters.json에 다음과 같은 코드를 밑에 삽입하기
```
      "rg_name": {
        "value": "${AZURE_RG_NAME}"
      },
      "app_name": {
        "value": "${AZURE_APPSERVICE_NAME}"
      }
```
- git bash에 입력해주기
```
AZURE_APPSERVICE_NAME=2번에서 만들었던 앱 서비스 플랜 이름
AZURE_RG_NAME=자신의 리소스 그룹 이름
```
7. settings에 secrete key 설정하기
   - Name : (echo $AZURE_ENV_NAME을 bash에 치면 확인 가능)
     hg(랜덤숫자)
   - Crendential key : (echo $AZURE_CREDENTIALS을 bash에 치면 확인 가능)
     {~~}
8. 배포하기
- 아래 명령어를 통해 코드를 푸시하고 GitHub 액션 워크플로우가 작동하는 것을 확인합니다.
```
git add .
git commit -m "Add GitHub Actions workflow"
git push origin
```
