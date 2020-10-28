package com.team2021grads.docsfox;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle ;

import java.awt.*;
import javafx.scene.control.Button;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;


public class DocsFox extends Application
{
    double starting_point_x, starting_point_y ;

    Group rectangle = new Group() ;

    Rectangle new_rectangle = null ;

    boolean new_rectangle_is_being_drawn = false ;

    Color[] rectangle_colors = { Color.TRANSPARENT };
    private java.awt.event.KeyEvent KeyEvent;

    //  The following method adjusts coordinates so that the rectangle
    //  is shown "in a correct way" in relation to the mouse movement.

    void adjust_rectangle_properties( double starting_point_x,
                                      double starting_point_y,
                                      double ending_point_x,
                                      double ending_point_y,
                                      Rectangle given_rectangle )
    {
        given_rectangle.setX( starting_point_x ) ;
        given_rectangle.setY( starting_point_y ) ;
        given_rectangle.setWidth( ending_point_x - starting_point_x ) ;
        given_rectangle.setHeight( ending_point_y - starting_point_y ) ;

        if ( given_rectangle.getWidth() < 0 )
        {
            given_rectangle.setWidth( - given_rectangle.getWidth() ) ;
            given_rectangle.setX( given_rectangle.getX() - given_rectangle.getWidth() ) ;
        }

        if ( given_rectangle.getHeight() < 0 )
        {
            given_rectangle.setHeight( - given_rectangle.getHeight() ) ;
            given_rectangle.setY( given_rectangle.getY() - given_rectangle.getHeight() ) ;
        }
    }


    public void start( Stage stage ) throws IOException {
        final int[] cPage = {0};
        PDDocument doc = PDDocument.load(new File("C:/Users/Oscar/IdeaProjects/Software Engineering Project/z_X10001 LLP Sheet 1 page.pdf"));
        PDFRenderer renderer = new PDFRenderer(doc);

        BufferedImage[] Pages = new BufferedImage[doc.getNumberOfPages()];
        for(int i = 0; i<Pages.length;i++)
        {
            Pages[i] = renderer.renderImage(i);
        }

        Image image = SwingFXUtils.toFXImage(Pages[0], null );
        //Creating an image
        //Image image = new Image(new FileInputStream("C://Users//User//Videos//Captures//LLP Page 2.png"));

        //Setting the image view
        ImageView imageView = new ImageView(image);

        Button button = new Button("Confirm");
        Button button2 = new Button("Next page");
        HBox hbox = new HBox();
        hbox.getChildren().add(button);
        hbox.getChildren().add(button2);

        button.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                System.out.println("Hello World");
            }
        });

        button2.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                if(cPage[0]+1 >= Pages.length)
                {
                    cPage[0] = 0;
                }
                else
                {
                    cPage[0] = cPage[0]+1;
                }
                imageView.setImage(SwingFXUtils.toFXImage(Pages[cPage[0]], null ));
            }
        });



        //Setting the position of the image
        imageView.setX(50);
        imageView.setY(25);

        //setting the fit height and width of the image view
        imageView.setFitHeight(455);
        imageView.setFitWidth(500);

        //Setting the preserve ratio of the image view
        imageView.setPreserveRatio(true);

        //Creating a Group object
        Group root = new Group(imageView, rectangle,hbox);

        //Creating a scene object
        Scene scene = new Scene(root, 600, 500);
        scene.setFill(Color.BEIGE);

        //Setting title to the Stage
        stage.setTitle("DocsFox");




        //-------------------------------------Rectangle----------------------------------------------
        scene.setOnMousePressed( ( MouseEvent event ) ->
        {
            if ( new_rectangle_is_being_drawn == false )
            {
                starting_point_x = event.getSceneX() ;
                starting_point_y = event.getSceneY() ;

                new_rectangle = new Rectangle() ;

                // A non-finished rectangle has always the same color.
                new_rectangle.setFill( Color.TRANSPARENT ) ; // Color when it's being drawn
                new_rectangle.setStroke( Color.BLACK ) ;

                rectangle.getChildren().add( new_rectangle ) ;

                new_rectangle_is_being_drawn = true ;
            }

            // I added this to make the old rectangle disappear and allow to make a new one
            if (new_rectangle_is_being_drawn = true )
            {
                rectangle.getChildren().remove( new_rectangle ) ;
                starting_point_x = event.getSceneX() ;
                starting_point_y = event.getSceneY() ;

                new_rectangle = new Rectangle() ;

                // A non-finished rectangle has always the same color.
                new_rectangle.setFill( Color.TRANSPARENT ) ; // Color when it's being drawn
                new_rectangle.setStroke( Color.BLACK ) ;

                rectangle.getChildren().add( new_rectangle ) ;
            }
        } ) ;

        scene.setOnMouseDragged( ( MouseEvent event ) ->
        {
            if ( new_rectangle_is_being_drawn == true )
            {
                double current_ending_point_x = event.getSceneX() ;
                double current_ending_point_y = event.getSceneY() ;


                adjust_rectangle_properties( starting_point_x,
                        starting_point_y,
                        current_ending_point_x,
                        current_ending_point_y,
                        new_rectangle ) ;
            }



        } ) ;

        /*Export Segment for Sprint2
        scene.setOnMouseReleased( (MouseEvent event) ->
                {
                    Robot robot = null;
                    try {
                        robot = new Robot();
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }

                    java.awt.Rectangle captureRect = new java.awt.Rectangle((int)new_rectangle.getX(),(int)new_rectangle.getY(),(int)new_rectangle.getWidth(),(int)new_rectangle.getHeight());
                    BufferedImage screenFullImage = robot.createScreenCapture(captureRect);
                    try {
                        ImageIO.write(screenFullImage, "jpg", new File("C:/Users/mkyn3/IdeaProjects/DocsFox/src/ClippedImgs/ci.jpg"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
           */

        stage.setScene(scene);
        stage.show();
    }



    public static void main( String[] command_line_parameters )
    {
        launch( command_line_parameters ) ;
    }
}