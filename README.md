DEPRECATED
Pls use an alternative like https://github.com/cortinico/slidetoact

# ProSwipeButton
A swipe button for Android with a circular progress bar for async operations

![](https://raw.githubusercontent.com/shadowfaxtech/proSwipeButton/master/proSwipeButton_demo.gif)

# Gradle
```
dependencies {
    ...
    compile 'in.shadowfax:proswipebutton:1.2.2'
}
```

# Usage
1. In your XML layout file, add this custom view
```xml
<in.shadowfax.proswipebutton.ProSwipeButton
        android:id="@+id/awesome_btn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="8dp"
        app:bg_color="@android:color/holo_blue_dark"
        app:btn_text="Place Order" />
```

2. React to successful swipe on the button by adding a swipe listener
```java
ProSwipeButton proSwipeBtn = (ProSwipeButton) findViewById(R.id.awesome_btn);
proSwipeBtn.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // user has swiped the btn. Perform your async operation now
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // task success! show TICK icon in ProSwipeButton
                        proSwipeBtn.showResultIcon(true); // false if task failed
                    }
                }, 2000);
            }
        });
```

3. After the async task is completed, tell the ProSwipeButton to show a result icon.
Either a tick for a successful async operation or cross for a failed async operation.

```java
proSwipeBtn.showResultIcon(true); //if task succeeds
proSwipeBtn.showResultIcon(false); //if task fails
```

# Customizations

You can customize the button via XML or programatically.

```XML
<in.shadowfax.proswipebutton.ProSwipeButton
        android:id="@+id/proswipebutton_main_error"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="8dp"
        app:arrow_color="#33FFFFFF"
        app:bg_color="@android:color/holo_blue_dark"
        app:btn_radius="2dp"
        app:btn_text="Place Order"
        app:text_color="@android:color/white"
        app:text_size="12sp" />
```
New: set distance the user must swipe to activate the button.

```JAVA
proswipebutton.setSwipeDistance(0.6f);
```

Feel free to raise feature requests via the issue tracker for more customizations or just send in a PR :)

# Sample
Clone the repository and check out the `app` module.

# License

```
MIT License

Copyright (c) 2017 Shadowfax Technologies

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
