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
        SAT, DRK, OTHER;
    }
    public enum Tool {
        PEN, PENCIL, CIRMARQUEE, SQRMARQUEE, BUCKET, DROPPER;
    }

    private ToggleGroup mToolToggleGroup;
    private int penSize;
    private Tool mTool;
    private FilterStyle mFilterStyle;
    private Color mColor;

    private double xPos, yPos, hPos, wPos;
    private double xSelectPos, ySelectPos, hSelectPos, wSelectPos;
    ArrayList<Shape> removeShapes = new ArrayList<>(1000);

    @FXML private ToggleButton tgbPencil;
    @FXML private ToggleButton tgbPen;
    @FXML private ToggleButton tgbSqrMarquee;
    @FXML private ToggleButton tgbCirMarquee;
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


            // imgView.setImage(Cc.getInstance().getImg());

            Cc.getInstance().setImageAndRefreshView(SwingFXUtils.toFXImage(bufferedImage, null));

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
    void mnuGrayscale(ActionEvent event) {


        if (Cc.getInstance().getImg() == null)
            return;

        //make sure that we set the image view first, so we can roll back and do other operations to it.
        Cc.getInstance().setImgView(this.imgView);

        Image greyImage = Cc.getInstance().transform(Cc.getInstance().getImg(), Color::grayscale);
        Cc.getInstance().setImageAndRefreshView(greyImage);


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
    @FXML
    void mnuUndo(ActionEvent event) {
        Cc.getInstance().undo();
    }
    @FXML
    void mnuRedo(ActionEvent event) {
        Cc.getInstance().redo();
    }

    //##################################################################
    //INITIALIZE METHOD
    //see: http://docs.oracle.com/javafx/2/ui_controls/jfxpub-ui_controls.htm
    //##################################################################
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Cc.getInstance().setImgView(this.imgView);
        bFilter.disableProperty().bind(Cc.getInstance().hasImgProperty());

        //Initializing Variables
        mToolToggleGroup = new ToggleGroup();
        penSize = 50;
        mTool = Tool.PENCIL;
        mFilterStyle = FilterStyle.DRK;
        mColor = Color.WHITE;

        //Selected Area Variables
        xSelectPos = 0;
        ySelectPos = 0;
        hSelectPos = imgView.getFitHeight();
        wSelectPos = imgView.getFitWidth();

        //Setting Values
        cboSome.getItems().addAll("Darker", "Saturate");
        cboSome.setValue("Darker");

        //Setting Toggle Group for the tool bar
        tgbPencil.setToggleGroup(mToolToggleGroup);
        tgbPen.setToggleGroup(mToolToggleGroup);
        tgbSqrMarquee.setToggleGroup(mToolToggleGroup);
        tgbCirMarquee.setToggleGroup(mToolToggleGroup);
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
                else if (newValue == tgbCirMarquee)
                    mTool = Tool.CIRMARQUEE;
                else if (newValue == tgbBucket)
                    mTool = Tool.BUCKET;
                else if (newValue == tgbDropper)
                    mTool = Tool.DROPPER;
            }
        });

        imgView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
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
                    Image snapshot = ancRoot.snapshot(snapshotParameters, null);
                    Cc.getInstance().setImageAndRefreshView(snapshot);
                    ancRoot.getChildren().removeAll(removeShapes);
                    removeShapes.clear();
                } else {
                    //do nothing right now
                }
                me.consume();
            }
        });

        imgView.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                if (mTool == Tool.PENCIL || mTool == Tool.PEN) {
                    xPos = me.getX();
                    yPos = me.getY();

                    Shape shape = new Circle(xPos, yPos, penSize);
                    switch (mTool) {
                        case PENCIL:
                            shape = new Circle(xPos, yPos, penSize);
                            break;
                        case PEN:
                            shape = new Rectangle(xPos, yPos, penSize, penSize);
                            break;
                    }
                    shape.setFill(mColor);
                    ancRoot.getChildren().add(shape);
                    removeShapes.add(shape);
                    me.consume();
                } else {
                    me.consume();
                    return;
                }
            }
        });

        //region Filter Button Listener
        bFilter.setOnAction(event -> {
            Image transformImage;
            switch (mFilterStyle) {
                case DRK:
                    //make darker
                    transformImage = Cc.getInstance().transform(Cc.getInstance().getImg(),
                            (x, y, c) -> (x > xSelectPos && x < wSelectPos)
                                    && (y > ySelectPos && y < hSelectPos) ? c.deriveColor(0, 1, .5, 1) : c
                    );
                    break;

                case SAT:
                    //saturate
                    transformImage = Cc.getInstance().transform(Cc.getInstance().getImg(),
                            (x, y, c) -> (x > xSelectPos && x < wSelectPos)
                                    && (y > ySelectPos && y < hSelectPos) ? c.deriveColor(0, 1.0 / .1, 1.0, 1.0) : c

                    );
                    break;

                default:
                    //make darker
                    transformImage = Cc.getInstance().transform(Cc.getInstance().getImg(),
                            (x, y, c) -> (x > xSelectPos && x < wSelectPos)
                                    && (y > ySelectPos && y < hSelectPos) ? c.deriveColor(0, 1, .5, 1) : c
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
