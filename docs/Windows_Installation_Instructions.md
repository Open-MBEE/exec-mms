# Instructions for Windows

This document describes how to configure and start up a running version of MMS 4.

Versions Used to Test these instructions
* [MMS v4.0.0-a2](https://github.com/Open-MBEE/mms/tree/4.0.0-a2)
* Windows 10 (64-bit) 10 Enterprise (v 10.0.17763 Build 17763)
* Docker Desktop (Community Edition) v2.3.0.5
  * Docker Engine v19.03.12
  * Docker-Compose v1.27.2

# Steps

## 1. Download and install Docker Desktop
> If you already have a fairly modern version of Docker Desktop installed ***you can skip this step***.

Download the [Docker Desktop Community 2.3.0.5](https://docs.docker.com/docker-for-windows/release-notes/#docker-desktop-community-2305) installer and follow the installation instructions.

## 2. Download and install Git for Windows
> If you already have Git ***you can skip this step***.

Go to the [Git for Windows](https://gitforwindows.org/) site and download the latest version.

This will not only install Git, but will also provide Git BASH, a shell command for working with Git in Windows.

## 3. Clone the MMS 4 Repository
> If you already cloned the repository, make sure you are on the correct branch/tag by following the checkout step (4).

Clone the repository and checkout the latest tagged version, as of the writing of these instructions.

1. Open GitBash

1. Navigate to the folder you want to clone the repo on, e.g., `C:\repos\mms`, but you may need to clone in your user folder if you do not have permission to create a new folder at the root drive level, ***for example***:

    ```bash
    cd /c/repos/mms
    ```
    
    > If you have to use a deeply nested folder, make sure you check out the section below on how to extend the Windows maximum path length.

1. Clone the `mms` repo

    ```bash
    git clone git@github.com:Open-MBEE/mms.git
    ```

1. Checkout the right branch/tag:

    ```bash
    git checkout 4.0.0-a2
    ```

## 4. Get setup
> IMPORTANT: Windows PowerShell is the recommended shell for the remaining steps.

1. Open "Windows PowerShell".
1. Navigate to the folder where you cloned the `mms` repository.
1. Copy the example MMS configuration file
   * `cp .\example\src\main\resources\application-test.properties .\example\src\main\resources\application.properties`
   > This configuration file is intended to work with the `docker-compose` file in the MMS repo
1. Make sure that `docker-compose` is available by running:

    ```bash
    docker-compose --version
    ```
    You should see something like this:
    ```bash
    docker-compose version 1.27.2, build 18f557f9
    ```

## 5. Start and Monitor MMS
1. Start up the containers

    ```bash
    docker-compose up -d
    ```

    You should see something like this:

    ```bash
    Creating network "mms_default" with the default driver
    Creating mms_postgres_1      ... done
    Creating mms_elasticsearch_1 ... done
    Creating mms                 ... done
    ```

    > If you are running this for the first time, you will see a much longer output.

    > If you modify the `application.properties` configuration file you will have to rebuild the `MMS` image by running: `docker-compose up build`

1. Make sure the containers are running:

    ```bash
    docker-compose ps
    ```

    You should see something like this:

    ```bash
           Name                      Command               State                       Ports
    -------------------------------------------------------------------------------------------------------------
    mms                   java -Djdk.tls.client.prot ...   Up      0.0.0.0:8080->8080/tcp
    mms_elasticsearch_1   /usr/local/bin/docker-entr ...   Up      0.0.0.0:9200->9200/tcp, 0.0.0.0:9300->9300/tcp
    mms_postgres_1        docker-entrypoint.sh postgres    Up      0.0.0.0:5432->5432/tcp
    ```

1. Or you can monitor what the server is doing by tailion and following the log for the `mms` container by:

    ```bash
    docker-compose logs -t -f mms
    ```

    which should show something like this:

    ```bash
    Attaching to mms
    mms              | 2020-10-01T14:51:42.184914325Z
    mms              | 2020-10-01T14:51:42.184964126Z   .   ____          _            __ _ _
    mms              | 2020-10-01T14:51:42.184976826Z  /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
    mms              | 2020-10-01T14:51:42.184980226Z ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
    mms              | 2020-10-01T14:51:42.184982726Z  \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
    mms              | 2020-10-01T14:51:42.184985026Z   '  |____| .__|_| |_|_| |_\__, | / / / /
    mms              | 2020-10-01T14:51:42.185004626Z  =========|_|==============|___/=/_/_/_/
    mms              | 2020-10-01T14:51:42.201916186Z  :: Spring Boot ::        (v2.2.6.RELEASE)
    mms              | 2020-10-01T14:51:42.201936886Z
    mms              | 2020-10-01T14:51:42.655378398Z 2020-10-01 14:51:42.646  INFO 1 --- [           main] o.o.sdvc.example.ExampleApplication      : Starting ExampleApplication on mms with PID 1 (/app.jar started by root in /mms)
    mms              | 2020-10-01T14:51:42.659855214Z 2020-10-01 14:51:42.656  INFO 1 --- [           main] o.o.sdvc.example.ExampleApplication      : The following profiles are active: test
    mms              | 2020-10-01T14:51:45.387823544Z 2020-10-01 14:51:45.386  INFO 1 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
    mms              | 2020-10-01T14:51:45.713567912Z 2020-10-01 14:51:45.713  INFO 1 --- [           main] .s.d.r.c.RepositoryConfiguratio
    ```

## 6. Use MMS

Refer to the MMS documentation, but to test if the service is running, you can go to: [`http://localhost:8080/healtcheck`](http://localhost:8080/healtcheck), which should show `healthy`.

### Check out the REST API

1. Go to [`http://localhost:8080/v3/swagger-ui.html`](http://localhost:8080/v3/swagger-ui.html) to see more info on the API.

1. Expand the `POST /authentication` accordion and click on `Try it out`

1. Set the Request body to be:
    > The values below are based on the `application-test.properties` configuration file that was copied above, and the values for: `sdvc.admin.username` and `sdvc.admin.password`, if you changed that, make sure the values below match what you put in the application file.

    ```json
    {
      "username": "test",
      "password": "test"
    }
    ```

1. Click the blue `Execute` button, and copy the `"token"` in the `Response body`, it should look somthing like this: `eyJhbGciOiJIUzI1NiJ9.6MTYwMTU4NTMxNDc0MSwiaWQiOiJ0ZXN0IiwiZW5hYmxlZCI6dHJ1ZSwiYXV0aG9yaXRpZXMiOlsibW1zYWRtaW4ieyJzdWIiOiJ0ZXN0IiwiY3JlYXRlZCILCJldmVyeW9uZSJdLCJleHAiOjE2MDE2NzE3MTR9.nRecPl65j72IbKDNn0m7BqebsxYT4WWHKRciM19L-NE`

1. At the top of the page click on the green outline `Authorize` button and paste the token into the `Value` textbox and click on the `Authorize` button.

    > You should see the `Authorize` button change to `Logout`.

1. Click the `Close` button, and you are now logged in the API.

    > All the endpoints should now show a closed black lock.

# Troubleshooting
This section covers some of the most critical issues you may face when setting up MMS in a Windows Environment.

## Windows Maximum Path Length Issue
By default Windows, does not allow paths to be longer than 260 characters.

If you see a copy error when building the custom CET JupyterLab or the installers in Windows, it may be because one of the paths is over 260 characters long.

### How to Fix This

1. Open the Registry Editor (`regedit.exe`) in **Admin mode**
    > If you do not have admin mode, you may be able to ask your support IT department to provide you with a local admin, or do this fix for you.
2. Navigate to: `HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\FileSystem`
3. Change the value of the `LongPathsEnabled` variable from `0` to `1`
4. Restart your computer and try again...
