package ca.tobymurray.fingerpainting.fingerpainting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.UUID;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private DrawingView m_drawingView;
    private ImageButton m_currentPaint;
    private ImageButton m_drawButton;
    private ImageButton m_eraseButton;
    private ImageButton m_newButton;
    private ImageButton m_saveButton;

    private float m_smallBrush;
    private float m_mediumBrush;
    private float m_largeBrush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_drawingView = (DrawingView) findViewById(R.id.drawing);
        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
        m_currentPaint = (ImageButton) paintLayout.getChildAt(0);
        m_currentPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        m_eraseButton = (ImageButton) findViewById(R.id.erase_btn);
        m_eraseButton.setOnClickListener(this);

        m_smallBrush = getResources().getInteger(R.integer.small_size);
        m_mediumBrush = getResources().getInteger(R.integer.medium_size);
        m_largeBrush = getResources().getInteger(R.integer.large_size);

        m_drawButton = (ImageButton) findViewById(R.id.draw_btn);
        m_drawButton.setOnClickListener(this);
        m_drawingView.setBrushSize(m_mediumBrush);

        m_newButton = (ImageButton) findViewById(R.id.new_btn);
        m_newButton.setOnClickListener(this);

        m_saveButton = (ImageButton) findViewById(R.id.save_btn);
        m_saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.draw_btn:
                drawButtonSelected();
                break;
            case R.id.erase_btn:
                eraseButtonSelected();
                break;
            case R.id.new_btn:
                newButtonSelected();
                break;
            case R.id.save_btn:
                saveButtonSelected();
                break;
        }
    }

    private void drawButtonSelected() {
        final Dialog brushDialog = new Dialog(this);
        brushDialog.setTitle("Brush size:");
        brushDialog.setContentView(R.layout.brush_chooser);
        brushDialog.show();

        ImageButton smallButton = (ImageButton) brushDialog.findViewById(R.id.small_brush);
        smallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_drawingView.setBrushSize(m_smallBrush);
                m_drawingView.setLastBrushSize(m_smallBrush);
                m_drawingView.setErase(false);
                brushDialog.dismiss();
            }
        });
        ImageButton mediumButton = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_drawingView.setBrushSize(m_mediumBrush);
                m_drawingView.setLastBrushSize(m_mediumBrush);
                m_drawingView.setErase(false);
                brushDialog.dismiss();
            }
        });
        ImageButton largeButton = (ImageButton) brushDialog.findViewById(R.id.large_brush);
        largeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_drawingView.setBrushSize(m_largeBrush);
                m_drawingView.setLastBrushSize(m_largeBrush);
                m_drawingView.setErase(false);
                brushDialog.dismiss();
            }
        });
    }

    private void eraseButtonSelected() {
        final Dialog brushDialog = new Dialog(this);
        brushDialog.setTitle("Eraser size:");
        brushDialog.setContentView(R.layout.brush_chooser);

        ImageButton smallButton = (ImageButton) brushDialog.findViewById(R.id.small_brush);
        smallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_drawingView.setErase(true);
                m_drawingView.setBrushSize(m_smallBrush);
                brushDialog.dismiss();
            }
        });
        ImageButton mediumButton = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_drawingView.setErase(true);
                m_drawingView.setBrushSize(m_mediumBrush);
                brushDialog.dismiss();
            }
        });
        ImageButton largeButton = (ImageButton) brushDialog.findViewById(R.id.large_brush);
        largeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_drawingView.setErase(true);
                m_drawingView.setBrushSize(m_largeBrush);
                brushDialog.dismiss();
            }
        });

        brushDialog.show();
    }

    public void newButtonSelected() {
        AlertDialog.Builder newDrawingDialog = new AlertDialog.Builder(this);
        newDrawingDialog.setTitle("New drawing");
        newDrawingDialog.setMessage("Start new drawing? (You will lose the current drawing)");
        newDrawingDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_drawingView.startNew();
                dialog.dismiss();
            }
        });
        newDrawingDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        newDrawingDialog.show();
    }

    private void saveButtonSelected() {
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Save drawing");
        saveDialog.setMessage("Save drawing to device gallery");
        saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_drawingView.setDrawingCacheEnabled(true);

                String imageSaved = MediaStore.Images.Media.insertImage(getContentResolver(), m_drawingView.getDrawingCache(), UUID.randomUUID().toString() + ".png", "drawing");

                if (imageSaved != null) {
                    Toast savedToast = Toast.makeText(getApplicationContext(), "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                    savedToast.show();
                } else {
                    Toast unsavedToast = Toast.makeText(getApplicationContext(), "Image could not be saved to the gallery at this time...", Toast.LENGTH_SHORT);
                    unsavedToast.show();
                }
                m_drawingView.destroyDrawingCache();
                dialog.dismiss();
            }
        });
        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        saveDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }

    public void paintClicked(View view) {
        if (view != m_currentPaint) {
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();
            m_drawingView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            m_currentPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            m_currentPaint = imgView;
            m_drawingView.setBrushSize(m_drawingView.getLastBrushSize());
        }
    }
}
