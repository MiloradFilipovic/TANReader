![App logo](https://i.imgur.com/89LaZBG.png)

# TAN table reader
Android application that reads bank TAN table entries. APK can be found [here](https://www.dropbox.com/s/kvcjiqi5p9ojafb/tan_reader_1.0.apk?dl=0).

## How it works
Application reads contents of a txt file which represents your TAN table used for bank's two-factor authentification. Each row in the TAN table file should be in a new line and the columns should be separated by one space.
The file can be located using the `Settings` screen.

### Example:

Your file should look something like this:
```
12354123 51321843 45312852 84321532
45312852 84321532 51321843 12354123
84321532 45312852 12354123 51321843
51321843 12354123 84321532 45312852
12354123 84321532 84321532 45312852
84321532 51321843 45312852 12354123
45312852 84321532 12354123 51321843
51321843 12354123 84321532 45312852
```

### Preview
![Application preview](https://i.imgur.com/ZKW0EdZ.png)