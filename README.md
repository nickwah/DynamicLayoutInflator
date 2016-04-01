# DynamicLayoutInflator
Inflate android XML layouts at runtime

Example: (see MainActivity.java for details)

```java
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout main = (RelativeLayout)findViewById(R.id.main_top);
        try {
            View view = DynamicLayoutInflator.inflateName(this, "testlayout");
            main.addView(view);
            // The above two lines could also be written like this, if you know that testlayout is in your assets:
            // View view = DynamicLayoutInflator.inflate(this, getAssets().open("testlayout.xml"), main);
            DynamicLayoutInflator.setDelegate(view, this);
            // If we have <TextView id="message" ... />, this is how to access it:
            TextView someTextView = (TextView)DynamicLayoutInflator.findViewByIdString("message");
        } catch (IOException e) {
            // This happens if getAssets().open() fails to find your layout
            e.printStackTrace();
        }

        // Alternatively, you can pass in a String of xml:
        RelativeLayout layout = (RelativeLayout)DynamicLayoutInflator.inflate(this, "<RelativeLayout width=\"match_parent\" height=\"match_parent\" />");
    }

    // Since we set this class as the delegate, anything in the layout that has onClick="ohHello(2)" will log "howdy number: 2"
    public void ohHello(int i) {
        Log.d("nick", "howdy number: " + i);
    }
```

## See also:

[iOS dynamic xml layouts](https://github.com/nickwah/NWLayoutInflator)

## License

Open source under the MIT license. See LICENSE for details.
