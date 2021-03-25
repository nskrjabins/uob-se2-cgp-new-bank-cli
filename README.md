# New Bank

New Bank is a Client-Server application that permits users to interact with the bank server through a command line interface.  

## Prequisites

### Java

This project assumes Java is pre-installed and available in your system. 
Download it here for [windows](https://www.java.com/en/download/help/windows_manual_download.html#download).

Follow this tutorial for [Mac OS by using brew](https://devqa.io/brew-install-java/).

### Maven 

1. For MAC OS. OS X prior to Mavericks (10.9) actually comes with Maven 3 built in.
 If you're on OS X Lion, you won't have java installed by default, if so run:
  
    **``brew install maven``**
 
2. For Windows follow: https://maven.apache.org/guides/getting-started/windows-prerequisites.html

3. For Linux follow: https://maven.apache.org/install.html

## About the project

Newbank CLI project is [Maven](https://maven.apache.org/what-is-maven.html) project. It's been made so the project can be easily build and follows industry standard for faster new developer onboarding and adoption.

NewBank CLI is a multi-module Maven Project, that way client and server can be easily build and test separately as they don't necessarily need to go hand in hand.

To get familiar with the Maven project structure visit [the Maven structure page](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html) 

## Getting Started

1. Clone the repo: `git clone git@github.com:nskrjabins/uob-se2-cgp-new-bank-cli.git`
2. Navigate to project folder `cd uob-se2-cgp-new-bank-cli`

## How to use New Bank

Start the Client and Server:

* Build the project by running ``mvn clean package``.
* Run the Server ``java -cp server/target/NewBankServer.jar newbank.server.NewBankServer``.
* Run the Client ``java -cp client/target/NewBankClient.jar newbank.client.ExampleClient``.

_The above won't be necessary if your using your IDE default builder or debugger._

The NewBank Client accepts the following commands:

* Use `SHOWMYACCOUNTS` to display all your accounts.
* Use `NEWACCOUNT <Name>` to add a new account.

## Code Style Guidelines

Follow [Google's Java Style](https://google.github.io/styleguide/javaguide)

