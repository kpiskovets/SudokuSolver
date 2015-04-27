package com.piskovets.sudokusolver;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity{


    public GridView gridView;
    private List<String> strList=new ArrayList<>();
    private SudokuGridAdapter adapter;
    List<List<Cell>> boardCheck=new ArrayList<>();
    CheckBox checkBox;
    SudokuGrid grid;
    boolean isValid=true;
    TextView stepTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT==Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.parseColor("#303F9F"));
        }
        checkBox=(CheckBox)findViewById(R.id.step_cb);
        stepTv=(TextView)findViewById(R.id.step_tv);
        for (int i = 0; i < 81; i++) {
            strList.add("");
        }

        boardCheck=getGrid(getEmptyGrid());
        findViewById(R.id.solve_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solve(v);
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
                clearBoard(v);
            }
        });
        gridView= (GridView) findViewById(R.id.gridView);
        adapter = new SudokuGridAdapter(this, strList);
        gridView.setAdapter(adapter);
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
                    ib.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Button b = (Button) v;
                            int row = position / 9;
                            int col = position % 9;
                            if (b.getText().equals("Delete")) {
                                tv.setText("");
                                tv.setTextColor(Color.parseColor("#3F51B5"));
                                tv.setBackgroundColor(Color.WHITE);

                            } else {
                                if (b.getText().equals("Back")) {
                                    tv.setText(tv.getText());


                                } else {
                                    tv.setText(b.getText());
                                }
                            }

                            if (!tv.getText().toString().equals("")) {
                                tv.setTextColor(Color.parseColor("#000000"));
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
    public void clearBoard(View v){
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                strList.set(i*9+j,"");
            }
        }
        adapter.notifyDataSetChanged();
        boardCheck=getGrid(getEmptyGrid());
        stepTv.setText("0");
        findViewById(R.id.plusbutton).setVisibility(View.INVISIBLE);
        findViewById(R.id.minusbutton).setVisibility(View.INVISIBLE);
        findViewById(R.id.step_tv).setVisibility(View.INVISIBLE);
        grid=null;
        isValid=true;
    }
    public void solve(View v){
        stepTv.setText("0");
        if(isValid) {
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
    public void onLongSolveClick(View v){
        Toast.makeText(getBaseContext(),"Solve",Toast.LENGTH_SHORT).show();
    }
    public void onLongClearClick(View v){
        Toast.makeText(getBaseContext(),"Clear grid",Toast.LENGTH_SHORT).show();
    }
    public void plusOnClick(View v){
        int n= Integer.parseInt(stepTv.getText().toString());
        n++;
        if(n>grid.m_populateList.size()){
            n=grid.m_populateList.size();
        }
        stepTv.setText(String.valueOf(n));
        boardCheck.get(grid.m_populateList.get(n-1).m_row).get(grid.m_populateList.get(n-1).col()).setVal(grid.m_populateList.get(n-1).val());
        updateGrid(boardCheck);
    }
    public void minusOnClick(View v){
        int n= Integer.parseInt(stepTv.getText().toString());
        if(n!=0){
            boardCheck.get(grid.m_populateList.get(n-1).m_row).get(grid.m_populateList.get(n-1).col()).setVal(0);}
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
            String[] cellChars = row.split(",");
            List<Cell> line = new ArrayList<>();
            for (String cellChar : cellChars) {

                if (cellChar.equals("_") ) {
                    line.add(new Cell(0));
                } else {
                    line.add(new Cell(Integer.parseInt(cellChar)));
                }
            }
            grid.add(line);
        }
        return grid;
    }


    private static String[] getEmptyGrid(){
        List<String> board = new ArrayList<String>() {
            private static final long serialVersionUID = 1L;
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
