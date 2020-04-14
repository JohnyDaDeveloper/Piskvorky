package cz.johnyapps.piskvorky.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import cz.johnyapps.piskvorky.GameModes;
import cz.johnyapps.piskvorky.entities.BoardSettings;
import cz.johnyapps.piskvorky.entities.Field;
import cz.johnyapps.piskvorky.services.PiskvorkyService;
import cz.johnyapps.piskvorky.shapes.NewGameStartsShape;
import cz.johnyapps.piskvorky.shapes.ShapeDrawer;
import cz.johnyapps.piskvorky.shapes.Shapes;
import cz.johnyapps.piskvorky.shapes.shape.Shape;

@SuppressWarnings("SuspiciousNameCombination")
public class PiskvorkyView extends View implements View.OnTouchListener, Shapes, GameModes {
    private static final String TAG = "PiskvorkyView";
    private static final int WAY_DIAGONAL_LEFT = -1;
    private static final int WAY_BELLOW = 0;
    private static final int WAY_DIAGONAL_RIGHT = 1;

    private BoardSettings boardSettings;
    private ShapeDrawer shapeDrawer;
    private Paint lastMovePaint;

    public PiskvorkyView(Context context) {
        super(context);
        initialize();
    }

    public PiskvorkyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public PiskvorkyView(Context context, AttributeSet attrs, int theme) {
        super(context, attrs, theme);
        initialize();
    }

    private void initialize() {
        PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();
        piskvorkyService.setOnNewGameListener(new PiskvorkyService.OnNewGameListener() {
            @Override
            public void onNewGame() {
                setSelfAsOnTouchListener();
            }
        });

        boardSettings = piskvorkyService.getBoardSettings();
        shapeDrawer = new ShapeDrawer(boardSettings.getShapeWidth(), boardSettings.getShapePadding());
        lastMovePaint = new Paint();
        lastMovePaint.setColor(Color.YELLOW);

        setOnTouchListener(this);
    }

    private void setSelfAsOnTouchListener() {
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();

            clickField(getFieldPosition(x, y));
            invalidate();

            return false;
        } else {
            return performClick();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.WHITE);

        PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();

        if (piskvorkyService.getFieldsArray() == null) {
            calculateFields();
        }

        fillLastMoveField(canvas);
        drawFields(canvas, boardSettings.getLineWidth());
        drawFilledFields(canvas);

        Shape shape;
        if ((shape = lookForWinner()) != NO_SHAPE) {
            finishGame(shape);

            if (onShapeWonListener != null) {
                onShapeWonListener.onShapeWon(shape);
            }
        }
    }

    private void fillLastMoveField(Canvas canvas) {
        Field field = PiskvorkyService.getInstance().getField(PiskvorkyService.getInstance().getLastMoveIndex());
        
        if (field != null) {
            canvas.drawRect(field.getWidthStart(), field.getHeightStart(), field.getWidthEnd(), field.getHeightEnd(), lastMovePaint);
        }
    }

    private OnShapeWonListener onShapeWonListener;
    public interface OnShapeWonListener {
        void onShapeWon(Shape shape);
    }

    public void setOnShapeWonListener(OnShapeWonListener onShapeWonListener) {
        this.onShapeWonListener = onShapeWonListener;
    }

    private void clickField(int index) {
        PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();

        if (piskvorkyService.getGameMode().equals(ONLINE)) {
            if (piskvorkyService.getMyPlayer() != piskvorkyService.getPlayingPlayerShape()) {
                Log.v(TAG, "clickField: not my move");
                return;
            }
        }

        ArrayList<Field> fields = piskvorkyService.getFieldsArray();
        Field field = fields.get(index);

        if (field.getShape() != NO_SHAPE) {
            Log.v(TAG, "clickField: invalid move");
            return;
        }

        field.setShape(piskvorkyService.getPlayingPlayerShape());
        piskvorkyService.setFields(fields);
        piskvorkyService.setLastMoveIndex(index);
        switchPlayer();
        piskvorkyService.updateGame(false);
    }

    private void restartGame() {
        Log.i(TAG, "Restarting...");

        calculateFields();

        PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();

        if (piskvorkyService.getGameMode().equals(GameModes.ONLINE)) {
            piskvorkyService.setPlayingPlayer(NewGameStartsShape.get());
        }

        setOnTouchListener(this);

        piskvorkyService.removeFields();
        piskvorkyService.setLastMoveIndex(-1);
        piskvorkyService.updateGame(true);

        invalidate();
    }

    private void finishGame(Shape shape) {
        Log.i(TAG, "Game finished (" + shape + " won)");

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    restartGame();

                    return false;
                } else {
                    return performClick();
                }
            }
        });
    }

    private void calculateFields() {
        Log.d(TAG, "Calcultaing fields...");
        PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();
        ArrayList<Field> fields = piskvorkyService.getFieldsArray();

        if (fields == null) {
            fields = new ArrayList<>();
        } else {
            fields.clear();
        }

        int width = getWidth() - boardSettings.getLineWidth();
        int height = getHeight() - boardSettings.getLineWidth();
        int padding = boardSettings.getLineWidth() / 2;

        float fieldDimen = (float) width / (float) boardSettings.getWidthFieldCount();
        int heightCount = height / (int) fieldDimen;

        for (int j = 0; j < heightCount; j++) {
            for (int i = 0; i < boardSettings.getWidthFieldCount(); i++) {
                fields.add(new Field(i * fieldDimen + padding,
                        (i + 1) * fieldDimen + padding,
                        j * fieldDimen + padding,
                        (j + 1) * fieldDimen + padding,
                        fieldDimen));
            }
        }

        piskvorkyService.setFields(fields);
        Log.d(TAG, fields.size() + " fields total");
    }

    private void drawFields(Canvas canvas, int strokeWidth) {
        Paint paint = new Paint(Color.BLACK);
        paint.setStrokeWidth(boardSettings.getLineWidth());

        int width = getWidth() - strokeWidth;
        int height = getHeight() - strokeWidth;
        int padding = strokeWidth / 2;
        int widthCount = boardSettings.getWidthFieldCount();

        float fieldDimen = (float) width / (float) widthCount;
        int heightCount = height / (int) fieldDimen;

        widthCount++;
        heightCount++;

        width += strokeWidth;
        height += strokeWidth;

        for (int i = 0; i < widthCount; i++) {
            float linePlace = fieldDimen * i + padding;
            canvas.drawLine(linePlace, 0, linePlace, height, paint);
        }

        for (int i = 0; i < heightCount; i++) {
            float linePlace = fieldDimen * i + padding;
            canvas.drawLine(0, linePlace, width, linePlace, paint);
        }
    }

    private void drawFilledFields(Canvas canvas) {
        PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();

        for (Field field : piskvorkyService.getFieldsArray()) {
            if (field.getShape() == NO_SHAPE) {
                continue;
            }

            shapeDrawer.drawShape(canvas, field);
        }
    }

    private void switchPlayer() {
        PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();

        if (piskvorkyService.getPlayingPlayerShape() == CROSS) {
            piskvorkyService.setPlayingPlayer(CIRCLE);
        } else {
            piskvorkyService.setPlayingPlayer(CROSS);
        }
    }

    private boolean isInField(Field field, float x, float y) {
        if (x >= field.getWidthStart() && x <= field.getWidthEnd()) {
            return y >= field.getHeightStart() && y <= field.getHeightEnd();
        }

        return false;
    }

    private int getFieldPosition(float x, float y) {
        PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();
        ArrayList<Field> fields = piskvorkyService.getFieldsArray();

        if (fields == null) {
            Log.e(TAG, "getFieldPosition: fields are null!");
            return -1;
        }

        for (int i = 0; i < fields.size(); i++) {
            if (isInField(fields.get(i), x, y)) {
                return i;
            }
        }

        return -1;
    }

    private Shape lookForWinner() {
        for (Shape shape : ALL_SHAPES) {
            if (lookForWiningShapeInLines(shape) ||
                    lookForWiningShapeInWay(shape, WAY_DIAGONAL_LEFT) ||
                    lookForWiningShapeInWay(shape, WAY_BELLOW) ||
                    lookForWiningShapeInWay(shape, WAY_DIAGONAL_RIGHT)) {

                return shape;
            }
        }

        return NO_SHAPE;
    }

    private boolean lookForWiningShapeInLines(Shape shape) {
        PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();
        ArrayList<Field> fields = piskvorkyService.getFieldsArray();

        if (fields == null) {
            Log.e(TAG, "lookForWiningShapeInLines: fields are null!");
            return false;
        }

        int strike = 0;

        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);

            if (i % boardSettings.getWidthFieldCount() == 0) {
                strike = 0;
            }

            if (field.getShape().getId() == shape.getId()) {
                strike++;
            } else {
                strike = 0;
            }

            if (strike == boardSettings.getValidStrike()) {
                return true;
            }
        }

        return false;
    }

    private boolean lookForWiningShapeInWay(Shape shape, int way) {
        PiskvorkyService piskvorkyService = PiskvorkyService.getInstance();
        ArrayList<Field> fields = piskvorkyService.getFieldsArray();

        if (fields == null) {
            Log.e(TAG, "lookForWiningShapeInWay: fields are null!");
            return false;
        }

        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);

            if (field.getShape().getId() == shape.getId()) {
                int strike = 1;
                int index = i;

                while (checkShapeInWay(index, way, fields)) {
                    strike++;
                    index += boardSettings.getWidthFieldCount() + way;

                    if (strike == boardSettings.getValidStrike()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkShapeInWay(int index, int way, ArrayList<Field> fields) {
        Field field = fields.get(index);
        int belowIndex = index + boardSettings.getWidthFieldCount() + way;

        if (belowIndex >= fields.size()) {
            return false;
        }

        return field.getShape() == fields.get(belowIndex).getShape();
    }
}
