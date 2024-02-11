package com.example.wgwsim2;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.acmerobotics.roadrunner.Vector2d;
import com.example.wgwsim2.databinding.ActivityWgwSim2Binding;
import com.example.wgwsimcore.WGWsimCore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class WgwSim2Activity extends AppCompatActivity { 

    private AppBarConfiguration appBarConfiguration;
    private ActivityWgwSim2Binding binding;

    boolean startNewRun = false;
    Bitmap fieldBMP;
    Bitmap workingBmp;
    Canvas workingCanvas;
    Paint workingPaint;

    ImageView ftcFieldView;
    TextView textView;
    TextView mouseTrackText;
    SeekBar seekBar;
    FloatingActionButton playPauseButton;
    Handler handler = new Handler();
    Menu optionsMenu;
    final int MENU_ID_ROBOT = 100;
    final int MAX_ROBOTS = 24;


    class EnabledPaths {
        int robotIndex;
        int trajIndex;
        boolean enabled;
        String name;
        EnabledPaths(int robotIndex, int trajIndex, boolean enabled, String name){
            this.robotIndex = robotIndex;
            this.trajIndex = trajIndex;
            this.enabled = enabled;
            this.name = name;
        }

    }
    LinkedHashMap<Integer, EnabledPaths> enabledPaths = new LinkedHashMap<>();


    // convert FTC field locaiton in inches to screen pixel locations
    Vector2d inch2Pixel (Vector2d inch) {
        int h = workingCanvas.getHeight();
        int w = workingCanvas.getWidth();

        double scale = Math.min(h, w) /  wgwSimCore.getFieldDimensionInches();
        int x = (int) (w / 2 + inch.x * scale);
        int y = (int) (h / 2 - inch.y * scale);

        return new Vector2d(x,y);
    }

    WGWsimCore wgwSimCore = new WGWsimCore() {
        @Override
        public void drawLineInches(Vector2d start, Vector2d end) {
            workingCanvas.drawLine((float)inch2Pixel(start).x, (float)inch2Pixel(start).y,
                                   (float)inch2Pixel(end).x, (float)inch2Pixel(end).y,
                                    workingPaint);
        }

        @Override
        public void drawCircleInches(double xInches, double yInches, double rInches) {
            //todo
        }

        @Override
        public void setColor(String color) {
            workingPaint.setColor(Color.parseColor(color));
        }

        @Override
        public void telemetryTextAddLine(String str) {
            textView.append(str);
        }

        @Override
        public void telemetryTextClear() {
            textView.setText("");  // got some new text so clear the display
        }
    };

    void setPausePLay(){
        if (wgwSimCore.isPaused()) {
            playPauseButton.setImageResource(R.drawable.play_icon);
        } else {
            // want   app:srcCompat="@android:drawable/ic_media_pause"
            playPauseButton.setImageResource(R.drawable.pause_icon);
        }

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityWgwSim2Binding binding = ActivityWgwSim2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        textView = findViewById(R.id.textView5);
        mouseTrackText = findViewById(R.id.MouseTrackText);

        ftcFieldView = findViewById(R.id.ftcFieldView);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        fieldBMP = BitmapFactory.decodeResource(getResources(), R.drawable.fieldsmall, options);
        ftcFieldView.setImageBitmap(fieldBMP); // put up the field view
        seekBar = findViewById(R.id.seekBar2);

        // a spinner lets user select what trajectory to run.
        //get the spinner from the xml.
        String[] spinnerItems = { "Left", "Center", "Right"};
        Spinner spinner = findViewById(R.id.spinner1);  // get the resource in the layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        // Apply the adapter to the spinner.
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String newItem = spinner.getSelectedItem().toString();
                int offset;
                switch (newItem) {
                    case "Left":
                        offset = 2;
                        break;
                    case "Center":
                        offset = 3;
                        break;
                    case "Right":
                        offset = 4;
                        break;
                    default: return;
                }
                for (int robotNum = 1; robotNum <= wgwSimCore.getNumRobots(); robotNum++) {   // all robots, clear telemetry
                    EnabledPaths path  =  enabledPaths.get(MENU_ID_ROBOT * robotNum + 1);
                    if (path != null) {
                        if (! path.enabled) // is robot enabled?
                            optionsMenu.performIdentifierAction(MENU_ID_ROBOT * robotNum + offset, 0);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });



        binding.PlayBlueSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewRun = true;
            }
        });

        playPauseButton = findViewById(R.id.PlayRedSide);  // get the resource in the layout

        binding.PlayRedSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Playing Red Side", Snackbar.LENGTH_LONG)
//                        .setAnchorView(R.id.PlayBlueSide)
//                        .setAction("Action", null).show();
                if (wgwSimCore.isPaused()) {
                    wgwSimCore.unpause();
                } else {
                    wgwSimCore.pauseAction();
                }
                setPausePLay();
            }
        });

        binding.ftcFieldView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();

                // convert the  mouse location back to field coordinates.
                // got help from https://stackoverflow.com/questions/8002298/android-imageview-get-coordinates-of-tap-click-regardless-of-scroll-location
                // Get the values of the matrix
                Matrix matrix = ((ImageView) view).getImageMatrix();
                float[] values = new float[9];
                matrix.getValues(values);

                // values[2] and values[5] are the x,y coordinates of the top left corner of the drawable image, regardless of the zoom factor.
                // values[0] and values[4] are the zoom factors for the image's width and height respectively. If you zoom at the same factor, these should both be the same value.
                // first find pixel locaiton in origional image, then scale to inches, then offset so 0,0 is center of field
                double fieldInches = wgwSimCore.getFieldDimensionInches();
                double relativeX = ( (x - values[2]) / values[0]) * fieldInches / fieldBMP.getHeight() - fieldInches / 2.0;
                double relativeY = (-(y - values[5]) / values[4]) * fieldInches / fieldBMP.getHeight() + fieldInches / 2.0;

                String formatedStr = String.format("clicked x: %.2f   y: %.2f\n", relativeX, relativeY);
                mouseTrackText.setText(formatedStr);

                return false;
            }
        } );


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            /**
             * seekBar   The SeekBar whose progress has changed
             * progress  The current progress level. This will be in the range 0..max where max was set by setMax(int). (The default value for max is 100.)
             * fromUser  True if the progress change was initiated by the user.
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
                if (fromUser){
                    double time = ((double)progress / seekBar.getMax()) * wgwSimCore.getActionDuration();
                    wgwSimCore.showAtTimeMs(time);
                    setPausePLay();
                }
            }
        });
        handler.post(updateTimerRunnable);  // start a timer tick!
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wgw_sim2, menu);

        int robotItemNum = MENU_ID_ROBOT;
        for (int robotNum = 0; robotNum< wgwSimCore.getNumRobots(); robotNum++) {   // all robots, clear telemetry
            String robotname = wgwSimCore.getRobotName(robotNum);
            menu.addSubMenu(0, robotItemNum, Menu.NONE, robotname);
            SubMenu submenu = menu.findItem(robotItemNum).getSubMenu();
            assert submenu != null;
            submenu.clear();
            List<String> names = wgwSimCore.getActionNames(robotNum);
            int subItem = robotItemNum+1;
            submenu.add(1,subItem /*subItem*/, Menu.NONE, "Disabled");
            enabledPaths.put(subItem++,new EnabledPaths(robotNum, -1,false, ""));
            int pathIndex = 0;
            for (String name : names) {   // add all the names of the paths.
                MenuItem menuItem = submenu.add(1,subItem, Menu.NONE, name);
                menuItem.setTitleCondensed(robotname);
                enabledPaths.put(subItem,new EnabledPaths(robotNum, pathIndex,false, name));
                subItem++;
                pathIndex++;
                if(pathIndex >= MENU_ID_ROBOT) // max number of paths reached.
                    break;
            }
            submenu.setGroupCheckable(1, true, true);

            // Robots that are not dissabled will have a path set
            // later by the spinner.setOnItemSelectedListener
            // robots that are dissabled will not have a path assigned by spinner and stay dissabled
            // until user picks a path for them
            if ( !wgwSimCore.getRobotEnabled(robotNum)) {
                int defaultItem = robotItemNum+1;
                MenuItem menuItem = menu.findItem(defaultItem);
                menuItem.setChecked(true);
                enabledPaths.get(defaultItem).enabled = true;
            }
            robotItemNum += MENU_ID_ROBOT;
        }
        optionsMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id >= MENU_ID_ROBOT && id < MENU_ID_ROBOT * MAX_ROBOTS) {
            int parentID = (id / MENU_ID_ROBOT) * MENU_ID_ROBOT;
            boolean checked ;
            if (id != parentID) {
                if (!item.isChecked()) {
                    item.setChecked(true);
                    checked = true;
                } else {
                    item.setChecked(false);
                    checked = false;
                }

                for (int i = parentID+1; i <= parentID+MENU_ID_ROBOT-1; i++) {
                    EnabledPaths path = enabledPaths.get(i);
                    if (path != null) {
                        path.enabled=false;  // clear them all out for this subset
                    } else{
                        break; // no more in this subset
                    }
                }
                EnabledPaths path = enabledPaths.get(id);
                if (path != null) {
                    Objects.requireNonNull(enabledPaths.get(id)).enabled = checked;
                    startNewRun = true;
                }
                if (item.getTitle() == "Disabled") {
                    startNewRun = true;
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_wgw_sim2);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


    Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {

            handler.postDelayed(this, wgwSimCore.getTimerTickms());

            if (startNewRun) {
                startNewRun = false;
                ftcFieldView.setImageBitmap(workingBmp); //
                wgwSimCore.clearRobotActions();
                Set<Integer> keys = enabledPaths.keySet();
                for (Integer key : keys) {
                    EnabledPaths path = enabledPaths.get(key);
                    if (path.enabled) {
                        wgwSimCore.setCurrentAction(path.robotIndex, path.name);
                    }
                }
                wgwSimCore.startAction();
            }

            workingBmp = fieldBMP.copy(fieldBMP.getConfig(), true);  // make a local copy
            workingCanvas = new Canvas(workingBmp);
            workingPaint = new Paint();
            workingPaint.setColor(Color.rgb(255, 16, 240));
            workingPaint.setStrokeWidth(10); //30
            workingPaint.setStyle(Paint.Style.STROKE);

            wgwSimCore.clearNewFieldOverlay();
            wgwSimCore.timerTick();
            if (wgwSimCore.isNewFieldOverlay()) {
                ftcFieldView.setImageBitmap(workingBmp);
            }

            double time = wgwSimCore.getCurrentTimeMs();
            seekBar.setProgress((int) ( (double)seekBar.getMax() * time / wgwSimCore.getActionDuration() ));

            time = wgwSimCore.getsimTimeMs();
            seekBar.setSecondaryProgress((int)( (double)seekBar.getMax() * time / wgwSimCore.getActionDuration() ));

        }
    };
}


