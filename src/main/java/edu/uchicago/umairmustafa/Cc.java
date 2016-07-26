package edu.uchicago.umairmustafa;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public class Cc {

    private static Cc stateManager;

    private Stage mMainStage, mSaturationStage;
    private ImageView imgView;
    private Image img;

    private BooleanProperty hasNoImage = new SimpleBooleanProperty(true);

    private static Stack<Image> undoImages;
    private static Stack<Image> redoImages;

    private Cc(){

    }

    public static Cc getInstance(){
        if(stateManager == null){
            stateManager = new Cc();
            undoImages = new Stack<>();
            redoImages = new Stack<>();
        }
        return stateManager;
    }

    //from Horstmann
    public static Image transform(Image in, UnaryOperator<Color> f) {
        int width = (int) in.getWidth();
        int height = (int) in.getHeight();
        WritableImage out = new WritableImage(
                width, height);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                out.getPixelWriter().setColor(x, y,
                        f.apply(in.getPixelReader().getColor(x, y)));
        return out;
    }

    public static <T> Image transform(Image in, BiFunction<Color, T, Color> f, T arg) {
        int width = (int) in.getWidth();
        int height = (int) in.getHeight();
        WritableImage out = new WritableImage(
                width, height);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                out.getPixelWriter().setColor(x, y,
                        f.apply(in.getPixelReader().getColor(x, y), arg));
        return out;
    }

    public static Image transform(Image in, ColorTransformer f) {
        int width = (int) in.getWidth();
        int height = (int) in.getHeight();
        WritableImage out = new WritableImage(
                width, height);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                out.getPixelWriter().setColor(x, y,
                        f.apply(x, y, in.getPixelReader().getColor(x, y)));
        return out;
    }

    public Stage getMainStage() {
        return mMainStage;
    }

    public void setMainStage(Stage mMainStage) {
        this.mMainStage = mMainStage;
    }

    public Stage getSaturationStage() {
        return mSaturationStage;
    }

    public void setSaturationStage(Stage mSaturationStage) {
        this.mSaturationStage = mSaturationStage;
    }

    public ImageView getImgView() {
        return imgView;
    }

    public void setImgView(ImageView imgView) {
        this.imgView = imgView;
    }

    public Image getImg() {
        return img;
    }

    public BooleanProperty hasImgProperty() {
        return hasNoImage;
    }

    public void undo(){
        if(!undoImages.isEmpty()){
            redoImages.push(this.img);
            this.img = undoImages.pop();
            imgView.setImage(img);
        }
    }

    public void redo(){
        if(!redoImages.isEmpty()){
            undoImages.push(this.img);
            this.img = redoImages.pop();
            imgView.setImage(img);
        }
    }

    public void setImageAndRefreshView(Image img){
        hasImgProperty().setValue(false);
        undoImages.push(this.img);
        this.img = img;
        imgView.setImage(img);
    }

    public void close(){
        imgView.setImage(null);
    }
}
