package edu.uchicago.umairmustafa;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SampleGeneric Skeleton for 'imageshop.fxml' Controller Class
 */


public class ImageShopController implements Initializable {

    public enum FilterStyle {
        SAT, DRK, GS;
    }
    public enum Tool {
        PEN, PENCIL, SQRMARQUEE, BUCKET, DROPPER;
    }

    private Tool mTool;
    private ToggleGroup mToolToggleGroup;

    private int penSize;
    private FilterStyle mFilterStyle;
    private Color mColor;

    private double xAnchor, yAnchor;//position of mouse for anchor using marquee tool
    ArrayList<Shape> removeShapes = new ArrayList<>(1000);

    Rectangle selectionRect;

    @FXML private ToggleButton tgbPencil;
    @FXML private ToggleButton tgbPen;
    @FXML private ToggleButton tgbSqrMarquee;
    @FXML private ToggleButton tgbBucket;
    @FXML private ToggleButton tgbDropper;
    @FXML private ImageView imgView;
    @FXML private ComboBox<String> cboSome;
    @FXML private ColorPicker cpkColor;
    @FXML private Slider sldSize;
    @FXML private AnchorPane ancRoot;
    @FXML private Button bFilter;

    @FXML
    void mnuOpenAction(ActionEvent event) {//http://java-buddy.blogspot.com/2013/01/use-javafx-filechooser-to-open-image.html

        Cc.getInstance().setImgView(this.imgView);
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        //Show open file dialog
        File file = fileChooser.showOpenDialog(null);
        //openFile(file);

        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            Cc.getInstance().setImageAndRefreshView(SwingFXUtils.toFXImage(bufferedImage, null));
            //Setting Selection to image after loading it
            selectionRect = new Rectangle(0, 0, imgView.getImage().getWidth(),imgView.getFitHeight());
            selectionRect.setFill(null);
            selectionRect.setVisible(false);
        } catch (IOException ex) {
            Logger.getLogger(ImageShopController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    @FXML
    void mnuReOpenLast(ActionEvent event) {
      //  Cc.getInstance().reOpenLast();
    }
    @FXML
    void mnuSaveAction(ActionEvent event) {

    }
    @FXML
    void mnuSaveAsAction(ActionEvent event) {

    }
    @FXML
    void mnuQuitAction(ActionEvent event) {
        System.exit(0);
    }
    @FXML
    void mnuCloseAction(ActionEvent event) {
        Cc.getInstance().close();
    }
    @FXML
    void mnuUndo(ActionEvent event) {
        Cc.getInstance().undo();
    }
    @FXML
    void mnuRedo(ActionEvent event) {
        Cc.getInstance().redo();
    }
    @FXML
    void mnuSaturate(ActionEvent event) {

        Cc.getInstance().setImgView(this.imgView);

        Stage dialogStage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/saturation.fxml"));
            Scene scene = new Scene(root);
            dialogStage.setTitle("Saturation");
            dialogStage.setScene(scene);
            //set the stage so that I can close it later.
            Cc.getInstance().setSaturationStage(dialogStage);
            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //##################################################################
    //INITIALIZE METHOD
    //see: http://docs.oracle.com/javafx/2/ui_controls/jfxpub-ui_controls.htm
    //##################################################################
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Cc.getInstance().setImgView(this.imgView);
        bFilter.disableProperty().bind(Cc.getInstance().hasImgProperty());

        //Initializing Class Variables
        mToolToggleGroup = new ToggleGroup();
        penSize = 50;
        mTool = Tool.PENCIL;
        tgbPencil.setSelected(true);
        mFilterStyle = FilterStyle.DRK;
        mColor = Color.WHITE;

        //Setting Combo Values
        cboSome.getItems().addAll("Darker", "Saturate", "GreyScale");
        cboSome.setValue("Darker");

        //Setting Selection to image view in the start
        selectionRect = new Rectangle(0, 0, imgView.getFitWidth(),imgView.getFitHeight());
        selectionRect.setFill(null);
        selectionRect.setVisible(false);

        //Setting Toggle Group for the tool bar
        tgbPencil.setToggleGroup(mToolToggleGroup);
        tgbPen.setToggleGroup(mToolToggleGroup);
        tgbSqrMarquee.setToggleGroup(mToolToggleGroup);
        tgbBucket.setToggleGroup(mToolToggleGroup);
        tgbDropper.setToggleGroup(mToolToggleGroup);

        mToolToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(newValue == tgbPencil)
                    mTool = Tool.PENCIL;
                else if (newValue == tgbPen)
                    mTool = Tool.PEN;
                else if (newValue == tgbSqrMarquee)
                    mTool = Tool.SQRMARQUEE;
                else if (newValue == tgbBucket)
                    mTool = Tool.BUCKET;
                else if (newValue == tgbDropper)
                    mTool = Tool.DROPPER;
            }
        });

        imgView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                if(mTool == Tool.SQRMARQUEE){
                    xAnchor = me.getX();
                    yAnchor = me.getY();
                }
                me.consume();
            }
        });

        imgView.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                if (mTool == Tool.PENCIL || mTool == Tool.PEN) {
                    System.out.println("mouse pressed! " + me.getSource());

                    SnapshotParameters snapshotParameters = new SnapshotParameters();
                    snapshotParameters.setViewport(new Rectangle2D(0, 0, imgView.getFitWidth(), imgView.getFitHeight()));
                    ancRoot.getChildren().remove(selectionRect);
                    Image snapshot = ancRoot.snapshot(snapshotParameters, null);
                    Cc.getInstance().setImageAndRefreshView(snapshot);

                    ancRoot.getChildren().removeAll(removeShapes);//TODO : Understand the reasoning behind removeShapes as it enables undo and redo.
                    ancRoot.getChildren().add(selectionRect);
                    removeShapes.clear();
                } else if (mTool == Tool.BUCKET) {
                    int xPos = (int) me.getX();
                    int yPos = (int) me.getY();

                    if((xPos > selectionRect.getX() && xPos < selectionRect.getX() + selectionRect.getWidth())
                                    && (yPos > selectionRect.getY() && yPos < selectionRect.getY() + selectionRect.getHeight())){

                        Image transformImage = Cc.getInstance().transform(Cc.getInstance().getImg(),
                                (x, y, c) -> (x > selectionRect.getX() && x < selectionRect.getX() + selectionRect.getWidth())
                                        && (y > selectionRect.getY() && y < selectionRect.getY() + selectionRect.getHeight()) ? mColor : c
                        );
                        Cc.getInstance().setImageAndRefreshView(transformImage);
                    }
                } else if (mTool == Tool.DROPPER){
                    int xPos = (int) me.getX();
                    int yPos = (int) me.getY();
                    mColor = Cc.getInstance().getImg().getPixelReader().getColor(xPos, yPos);
                    cpkColor.setValue(mColor);
                }
                me.consume();
            }
        });

        imgView.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                if (mTool == Tool.PENCIL || mTool == Tool.PEN) {
                    double xPos = me.getX();
                    double yPos = me.getY();

                    if ((xPos > selectionRect.getX() && xPos < selectionRect.getX() + selectionRect.getWidth()) &&
                            (yPos > selectionRect.getY() && yPos < selectionRect.getY() + selectionRect.getHeight())) {

                        double tempPenSize = penSize;

                        if ((xPos + penSize) > (selectionRect.getX() + selectionRect.getWidth())){
                            tempPenSize = tempPenSize < (selectionRect.getX() + selectionRect.getWidth()) - xPos ? tempPenSize : (selectionRect.getX() + selectionRect.getWidth()) - xPos;
                        }

                        if ((yPos + penSize) > (selectionRect.getY() + selectionRect.getHeight())){
                            tempPenSize = tempPenSize < (selectionRect.getY() + selectionRect.getHeight()) - yPos ? tempPenSize : (selectionRect.getY() + selectionRect.getHeight()) - yPos;
                        }

                        if ((xPos - penSize) < selectionRect.getX()){
                            tempPenSize = tempPenSize < (xPos - selectionRect.getX()) ? tempPenSize : (xPos - selectionRect.getX());
                        }

                        if ((yPos - penSize) < selectionRect.getY()){
                            tempPenSize = tempPenSize < (yPos - selectionRect.getY()) ? tempPenSize : (yPos - selectionRect.getY());
                        }

                        Shape shape = new Circle(xPos, yPos, tempPenSize);

                        switch (mTool) {
                            case PENCIL:
                                shape = new Circle(xPos, yPos, tempPenSize);
                                break;
                            case PEN:
                                shape = new Rectangle(xPos, yPos, tempPenSize, tempPenSize);
                                break;
                        }
                        shape.setFill(mColor);
                        ancRoot.getChildren().add(shape);
                        removeShapes.add(shape);
                    }
                } else if(mTool == Tool.SQRMARQUEE) {
                    double xPos = me.getX();
                    double yPos = me.getY();

                    double x = xPos > xAnchor ? xAnchor : xPos;
                    double y = yPos > yAnchor ? yAnchor : yPos;
                    double width = xPos > xAnchor ? xPos - xAnchor : xAnchor - xPos;
                    double height = yPos > yAnchor ? yPos - yAnchor : yAnchor - yPos;

                    Rectangle rectangle = new Rectangle(x, y, width, height);
                    rectangle.setFill(null);
                    rectangle.setStroke(Color.BLACK);
                    rectangle.setStrokeWidth(1);
                    rectangle.getStrokeDashArray().addAll(10d);

                    ancRoot.getChildren().add(rectangle);
                    ancRoot.getChildren().remove(selectionRect);
                    selectionRect = rectangle;
                    selectionRect.setVisible(true);
                }
                me.consume();
            }
        });

        //region Filter Button Listener
        bFilter.setOnAction(event -> {
            if(Cc.getInstance().getImg() == null)
                return;

            Image transformImage;
            switch (mFilterStyle) {
                case DRK://Darker
                    transformImage = Cc.getInstance().transform(Cc.getInstance().getImg(),
                            (x, y, c) -> (x > selectionRect.getX() && x < selectionRect.getX() + selectionRect.getWidth())
                                    && (y > selectionRect.getY() && y < selectionRect.getY() + selectionRect.getHeight()) ? c.deriveColor(0, 1, .5, 1) : c
                    );
                    break;

                case SAT://Saturate
                    transformImage = Cc.getInstance().transform(Cc.getInstance().getImg(),
                            (x, y, c) -> (x > selectionRect.getX() && x < selectionRect.getX() + selectionRect.getWidth())
                                    && (y > selectionRect.getY() && y < selectionRect.getY() + selectionRect.getHeight()) ? c.deriveColor(0, 1.0 / .1, 1.0, 1.0) : c

                    );
                    break;

                case GS://GreyScale
                    transformImage = Cc.getInstance().transform(Cc.getInstance().getImg(), Color::grayscale);
                    break;

                default://Darker
                    transformImage = Cc.getInstance().transform(Cc.getInstance().getImg(),
                            (x, y, c) -> (x > selectionRect.getX() && x < selectionRect.getX() + selectionRect.getWidth())
                                    && (y > selectionRect.getY() && y < selectionRect.getY() + selectionRect.getHeight()) ? c.deriveColor(0, 1, .5, 1) : c
                    );
                    break;
            }
            Cc.getInstance().setImageAndRefreshView(transformImage);
        });
        //endregion

        cpkColor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mColor = cpkColor.getValue();
            }
        });

        sldSize.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double temp  = (Double) newValue;
                penSize = (int) Math.round(temp);
            }
        });

        cboSome.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                switch (newValue){
                    case "Saturate":
                        mFilterStyle = FilterStyle.SAT;
                        break;
                    case "Darker":
                        mFilterStyle = FilterStyle.DRK;
                        break;
                    case "GreyScale":
                        mFilterStyle = FilterStyle.GS;
                        break;
                    default:
                        mFilterStyle = FilterStyle.DRK;
                        break;

                }
            }
        });

    }//END INIT

    //invert

//    Cc.getInstance().setSaturationLevel((int)sldSaturation.getValue());
//
//
//    int nLevel = Cc.getInstance().getSaturationLevel();
//    double dLevel = (100-nLevel)/100;
//
//    //saturation value
//    Image image = Cc.transform(Cc.getInstance().getImg(), (Color c, Double d) -> c.deriveColor(0, 1.0/ d, 1.0, 1.0), dLevel);
//    Cc.getInstance().getImgView().setImage(image);
//
//    Cc.getInstance().getSaturationStage().close();

}
