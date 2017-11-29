# Network Monitor Library

Android library for monitoring the network usage of your app. This allows you to set listeners on 
pre-defined thresholds for the network usage (Wifi or mobile data). 

Our use case for this library is to be able to warn users (and our backend too) when they're consuming 
too much data from our app. The app in question can be data intensive due to extensive use of 
videos and web views.

The library is compatible since Android `TODO`. 

## Getting Started

Include [Trinity Mirror Bitbucket Maven Repository](bitbucket.org/trinitymirror-ondemand/trinity-maven-respository/)
in your ``build.gradle``:

```groovy
repositories {
    maven {
        url "https://api.bitbucket.org/1.0/repositories/trinitymirror-ondemand/trinity-maven-respository/raw/snapshots"
        credentials {
            username '<Your_bitbucket_user>'
            password '<Your_bitbucket_password>'
        }
    }
    maven {
        url "https://api.bitbucket.org/1.0/repositories/trinitymirror-ondemand/trinity-maven-respository/raw/releases"
        credentials {
            username '<Your_bitbucket_user>'
            password '<Your_bitbucket_password>'
        }
    }
}
```

Include in your gradle file: 

```groovy
compile 'com.trinitymirror.network-monitor:network-monitor:<latest-version>'
```

## Usage

```java

class MyApplication extends Application {
    
    void onCreate() {
        // other stuff...
        
        // Setup and create listener 
        new NetworkMonitorServiceLocator.Config(appContext)
                .withJobExecutionWindow( // optional
                                (int) TimeUnit.MINUTES.toSeconds(2),
                                (int) TimeUnit.MINUTES.toSeconds(1));
        
        dataTeamListener = new UsageListener(
                LISTENER_ID_DATA_TEAM, 
                new UsageListener.Params(
                        1024 * 1024 * 512,       // 512 Mb
                        1024 * 1024 * 1024 * 2L, // 2 GB
                        TimeUnit.DAYS.toMillis(10),
                        UsageListener.NetworkType.MOBILE, 
                this::onDataTeamListenerResult));
        
        // Register listener
        final NetworkMonitor networkMonitor = NetworkMonitor.with();
        if (!networkMonitor.hasPermissions(context)) {
            Timber.d("NetworkMonitor permissions=false");
            return;
        }
        networkMonitor.registerListener(userListener);
        
        // ...
        // Unregister listener
        NetworkMonitor.with().unregisterListener(dataTeamListener);
        
        // ...
        // Open permissions dialog
        NetworkMonitor.with()
                .openPermissionsDialog(activity,
                        activity.getString(R.string.app_name),
                        R.mipmap.ic_launcher_round, permissionDialogResult);
    }
    
}
```

## Backwards compatibility

For backwards compatibility, this library uses:

* API < 23: Not supported
* API >= 23: NetworkStatsManager, check every 2h
* API >= 24: NetworkStatsManager#registerUsageCallback, resets after app restart

## Internals

* Check and request permissions

* Trigger warning after:

  * X Mb caused by the app, for a particular duration (API >= 23)
  * X Mb since app restart (API >= 24)

* Init:

  * Store mandatory thresholds
  * Store listeners
  * Register network callback  (if API available)
  * Schedule job every 2h (if permission granted)

* NetworkMonitor Job: _If_ mobile traffic > threshold, _Then_ notify listeners

## Running the tests

To build the project and run all unit tests

```
./gradlew build
```

## Deployment

This library is setup to be published into 
[Trinity Mirror Bitbucket Maven Repository](bitbucket.org/trinitymirror-ondemand/trinity-maven-respository/)

In order to publish a new version, we usually run an utility script: 

```
sh publish_and_update_version.sh <next-version>
```

> Running this script will publish the version currently specified in the 
`gradle.properties` file. The parameter <next-version>, is the next planned release version.

To release a snapshot version of the library, append `-SNAPSHOT` to the version specified in 
the `gradle.properties` file. 
Example: `VERSION=1.0.5.1-SNAPSHOT`

## Versioning

We use [SemVer](http://semver.org/) for versioning. 

## Credits

* [Lorenzo Quiroli blog post](https://medium.com/@quiro91/build-a-data-usage-manager-in-android-e7991cfe7fe4)
* [RobertZagorski helper classes](https://github.com/RobertZagorski/NetworkStats)
* [SO: How to get the mobile data usage for my app (e.g. using TrafficStats)](https://stackoverflow.com/a/19435738/1383284)