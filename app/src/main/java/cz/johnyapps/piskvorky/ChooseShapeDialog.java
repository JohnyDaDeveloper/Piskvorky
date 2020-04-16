package cz.johnyapps.piskvorky;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import cz.johnyapps.piskvorky.shapes.Shapes;
import cz.johnyapps.piskvorky.shapes.shape.Shape;

public class ChooseShapeDialog {
    private static final String TAG = "ChooseShapeDialog";

    private Context context;
    private int index = 0;
    private Shape[] shapes = Shapes.ALL_SHAPES;

    public ChooseShapeDialog(Context context) {
        this.context = context;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.dialog_choose_shape)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selected();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
        setupButtons(dialog);
    }

    private void setupButtons(AlertDialog dialog) {
        View left = dialog.findViewById(R.id.leftButton);
        View right = dialog.findViewById(R.id.rightButton);
        final ImageView imageView = dialog.findViewById(R.id.shapeImage);

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shiftShowedShape(-1, imageView);
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shiftShowedShape(1, imageView);
            }
        });
    }

    private void shiftShowedShape(int shift, ImageView imageView) {
        index += shift;

        if (index < 0) {
            index = shapes.length - 1;
        } else if (index >= shapes.length) {
            index = 0;
        }

        imageView.setImageResource(shapes[index].getDrawable());
    }

    private void selected() {
        Log.d(TAG, "selected: " + shapes[index]);
    }
}
