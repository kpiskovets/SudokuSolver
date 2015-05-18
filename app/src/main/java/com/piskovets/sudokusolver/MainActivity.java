package com.piskovets.sudokusolver;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class MainActivity extends Activity{


    public GridView gridView;
    private List<String> strList=new ArrayList<>();
    private SudokuGridAdapter adapter;
    List<List<Cell>> boardCheck=new ArrayList<>();
    CheckBox checkBox;
    SudokuGrid grid;
    boolean isValid=true;
    TextView stepTv;
    Typeface custom_font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT==Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.parseColor("#303F9F"));
            Bitmap icon = ((BitmapDrawable) getDrawable(R.mipmap.sudoku_logo)).getBitmap();
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(getString(R.string.app_name), icon, Color.parseColor("#303F9F"));
            this.setTaskDescription(taskDescription);
        }
        checkBox=(CheckBox)findViewById(R.id.step_cb);
        stepTv=(TextView)findViewById(R.id.step_tv);
        for (int i = 0; i < 81; i++) {
            strList.add("");
        }

        custom_font = Typeface.createFromAsset(getAssets(), "old_english_text_mt.ttf");
        boardCheck=getGrid(getEmptyGrid());
        findViewById(R.id.solve_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSolveClick(v);
            }
        });
        findViewById(R.id.solve_button).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onLongSolveClick(v);
                return true;
            }
        });
        findViewById(R.id.clear_button).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onLongClearClick(v);
                return true;
            }
        });
        findViewById(R.id.clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClearClick(v);
            }
        });
        findViewById(R.id.import_button).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onLongImportClick(v);
                return true;
            }
        });
        findViewById(R.id.import_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImportClick(v);
            }
        });
        gridView= (GridView) findViewById(R.id.gridView);
        adapter = new SudokuGridAdapter(this, strList);
        adapter.setTypeface(custom_font);
        gridView.setAdapter(adapter);
        clearGridView();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, final int position,
                                    long id) {
                FrameLayout fl = (FrameLayout) view;
                final TextView tv = (TextView) fl.findViewById(R.id.textView);

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.keyboard);
                dialog.setTitle("Pick a Number:");


                dialog.show();
                Context mContext = getBaseContext();

                String[] idOfButtons = {"button1", "button2", "button3", "button4", "button5", "button6", "button7", "button8", "button9", "button10", "button12"};
                for (String idOfButton : idOfButtons) {

                    Integer btnId = mContext.getResources().getIdentifier(idOfButton, "id", getBaseContext().getPackageName());
                    Button ib = (Button) dialog.findViewById(btnId);
                    ib.setTypeface(custom_font);
                    ib.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Button b = (Button) v;
                            int row = position / 9;
                            int col = position % 9;
                            Log.d("Width", String.valueOf(tv.getWidth()));
                            Log.d("Height", String.valueOf(tv.getHeight()));
                            if (b.getText().equals("Delete")) {
                                tv.setText("");
                                tv.setTextColor(Color.BLUE);
                                tv.setBackgroundColor(Color.WHITE);

                            } else {
                                if (b.getText().equals("Back")) {
                                    tv.setText(tv.getText());


                                } else {
                                    tv.setTextColor(Color.BLACK);
                                    tv.setText(b.getText());
                                }
                            }

                            if (!tv.getText().toString().equals("")) {
                                tv.setTextColor(Color.BLACK);
                                tv.setTypeface(custom_font);
                                boardCheck.get(row).get(col).setVal(Integer.valueOf(tv.getText().toString()));
                                if (boardIsValid()) {
                                    boardCheck.get(row).get(col).setIsValid(true);
                                    isValid = true;
                                    if(grid==null){
                                    updateGrid(boardCheck);}
                                    else{
                                        updateGrid(grid);
                                    }
                                } else {
                                    boardCheck.get(row).get(col).setIsValid(false);
                                    isValid = false;
                                    tv.setBackgroundColor(Color.parseColor("#d50000"));
                                }

                            } else {
                                boardCheck.get(row).get(col).setVal(0);
                                boardCheck.get(row).get(col).setIsValid(false);
                                if (boardIsValid()) {
                                    if(grid==null){
                                        updateGrid(boardCheck);}
                                    else{
                                        updateGrid(grid);
                                    }
                                    isValid = true;
                                }
                            }
                            dialog.dismiss();
                        }
                    });


                }
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(grid!=null){
                    findViewById(R.id.plusbutton).setVisibility(View.VISIBLE);
                    findViewById(R.id.minusbutton).setVisibility(View.VISIBLE);
                    findViewById(R.id.step_tv).setVisibility(View.VISIBLE);
                    updateGrid(boardCheck);}
                }else{
                    if(grid!=null){
                        updateGrid(grid);
                    }
                    findViewById(R.id.plusbutton).setVisibility(View.INVISIBLE);
                    findViewById(R.id.minusbutton).setVisibility(View.INVISIBLE);
                    findViewById(R.id.step_tv).setVisibility(View.INVISIBLE);
                }
            }
        });

    }
    public void onImportClick(View v){
        onClearClick(v);
        try {
            boardCheck=readGrid(getRandomPuzzle("puzzles"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(boardIsValid()){

            updateGrid(boardCheck);

        }
        else
            Toast.makeText(getBaseContext(),"Can't solve this sudoku",Toast.LENGTH_SHORT).show();

    }
    public void onClearClick(View v){

        clearGridView();
        gridView.setEnabled(true);

        boardCheck=getGrid(getEmptyGrid());
        updateGrid(boardCheck);
        stepTv.setText("0");
        findViewById(R.id.plusbutton).setVisibility(View.INVISIBLE);
        findViewById(R.id.minusbutton).setVisibility(View.INVISIBLE);
        findViewById(R.id.step_tv).setVisibility(View.INVISIBLE);
        grid=null;
        isValid=true;
    }
    public void onSolveClick(View v){
        adapter.notifyDataSetChanged();
        stepTv.setText("0");
        if(isValid) {
            gridView.setEnabled(false);
            grid = SudokuGrid.getGrid(boardCheck);
            grid.solve();
            Toast.makeText(getBaseContext(),"Solved",Toast.LENGTH_SHORT).show();
            if(checkBox.isChecked()){
                findViewById(R.id.plusbutton).setVisibility(View.VISIBLE);
                findViewById(R.id.minusbutton).setVisibility(View.VISIBLE);
                findViewById(R.id.step_tv).setVisibility(View.VISIBLE);
            }else{
                updateGrid(grid);}

        }else{
            Toast.makeText(getBaseContext(),"Can't solve this sudoku",Toast.LENGTH_SHORT).show();
        }

    }
    public void onLongImportClick(View v){
        Toast.makeText(getBaseContext(),"Random Puzzle",Toast.LENGTH_SHORT).show();
    }
    public void onLongSolveClick(View v){
        Toast.makeText(getBaseContext(),"Solve",Toast.LENGTH_SHORT).show();
    }
    public void onLongClearClick(View v){
        Toast.makeText(getBaseContext(),"Clear grid",Toast.LENGTH_SHORT).show();
    }
    public void plusOnClick(View v){
        int n= Integer.parseInt(stepTv.getText().toString());
        n++;
        if(n>grid.populateList.size()){
            n=grid.populateList.size();
        }
        stepTv.setText(String.valueOf(n));
        boardCheck.get(grid.populateList.get(n-1).row).get(grid.populateList.get(n-1).col()).setVal(grid.populateList.get(n-1).val());
        updateGrid(boardCheck);
    }
    public void minusOnClick(View v){
        int n= Integer.parseInt(stepTv.getText().toString());
        if(n!=0){
            boardCheck.get(grid.populateList.get(n-1).row).get(grid.populateList.get(n-1).col()).setVal(0);}
        updateGrid(boardCheck);
        n--;
        if(n<0){
            n=0;
        }
        stepTv.setText(String.valueOf(n));
    }

    public void updateGrid(SudokuGrid grid){


        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                strList.set(i * 9 + j, grid.getValue(i, j));
            }
        }
        adapter.notifyDataSetChanged();
    }
    public void updateGrid(List<List<Cell>> grid){
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(grid.get(i).get(j).val()==0){
                    strList.set(i * 9 + j, "");
                }else{
                    strList.set(i * 9 + j, String.valueOf(grid.get(i).get(j).val()));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    protected boolean boardIsValid(){
        for(int row=0;row<9;row++){
            for(int col=0;col<9;col++){
                if(boardCheck.get(row).get(col).val()!=0 && !valid(row,col,boardCheck.get(row).get(col).val())){
                    return false;
                }
            }
        }
        return true;
    }
    protected boolean valid(int row, int col,int k) {

        for(int ind = 0; ind < 9; ind++) {
            if(( boardCheck.get(ind).get(col).val()!=0 && row != ind && !boardCheck.get(ind).get(col).isValid() && boardCheck.get(ind).get(col).val()==k) || (boardCheck.get(row).get(ind).val()!=0 && col != ind && !boardCheck.get(row).get(ind).isValid() && boardCheck.get(row).get(ind).val()==k))
                return false;
        }
        int m = (row/3) * 3;
        int n = (col/3) * 3;
        for(int i = m; i < m + 3; i++)
            for(int j = n; j < n + 3; j++) {
                if(i == row && j == col) continue;
                if(boardCheck.get(i).get(j).val()!=0 &&  !this.boardCheck.get(i).get(j).isValid() && boardCheck.get(i).get(j).val()==k)
                    return false;
            }
        return true;
    }
    private List<List<Cell>> getGrid(String[] gridInput){
        List<List<Cell>> grid=new ArrayList<>();

        for (String row : gridInput) {
            String[] cellValues = row.split(",");
            List<Cell> line = new ArrayList<>();
            for (String cellValue : cellValues) {

                if (cellValue.equals("_")||cellValue.equals(".") ) {
                    line.add(new Cell(0));
                } else {
                    line.add(new Cell(Integer.parseInt(cellValue)));
                }
            }
            grid.add(line);
        }
        return grid;
    }
    public String getRandomPuzzle(String strFile) throws IOException {
        AssetManager am = getAssets();
        am.list("");
        InputStream is = am.open(strFile + ".txt");

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        List<String> strArray = new ArrayList<>();

        while ((line = br.readLine()) != null) {
            strArray.add(line);
        }

        br.close();
        Collections.shuffle(strArray, new Random());
        return strArray.get(0);
    }

    public View getViewByPosition(int pos, GridView gridView) {
        final int firstListItemPosition = gridView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + gridView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return gridView.getAdapter().getView(pos, null, gridView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return gridView.getChildAt(childIndex);
        }
    }

    public void clearGridView(){
        FrameLayout fl;
        TextView tv;

        for(int i=0;i<81;i++){
            fl=(FrameLayout)getViewByPosition(i,gridView );
            tv=(TextView)fl.findViewById(R.id.textView);
            tv.setTypeface(custom_font);
            tv.setTextColor(Color.BLACK);
            fl.refreshDrawableState();
        }
    }
    private  String[] getEmptyGrid(){
        List<String> board = new ArrayList<String>() {
            {add("_,_,_,_,_,_,_,_,_"); }
            {add("_,_,_,_,_,_,_,_,_"); }
            {add("_,_,_,_,_,_,_,_,_"); }
            {add("_,_,_,_,_,_,_,_,_"); }
            {add("_,_,_,_,_,_,_,_,_"); }
            {add("_,_,_,_,_,_,_,_,_"); }
            {add("_,_,_,_,_,_,_,_,_"); }
            {add("_,_,_,_,_,_,_,_,_"); }
            {add("_,_,_,_,_,_,_,_,_"); }
        };
        return board.toArray(new String[board.size()]);
    }
    private List<List<Cell>> readGrid(String input){
        List<List<Cell>> grid=new ArrayList<>();

        int index = 0;
        for (int row=0; row<9; row++) {
            List<Cell> line = new ArrayList<>();
            for (int column=0; column<9; column++) {
                char cellValue = input.charAt(index++);
                if (cellValue == '.'||cellValue=='_')
                    line.add(new Cell(0));
                else
                    line.add(new Cell(Integer.parseInt(String.valueOf(cellValue))));
            }
            grid.add(line);
        }
        return grid;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }
}
