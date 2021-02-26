package views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoveProductView extends BaseView {
    
    private final Map<String, Product> productMap;
    private final List<String> ids;
    
    private final Text tTotalProfit;
    private final ListView<String> lvProducts;
    private final Button btnRemove;
    private final Button btnRemoveAll;
    
    public RemoveProductView(Map<String, Product> productMap) {
        this.productMap = productMap;
        ids = new ArrayList<>();
        tTotalProfit = new Text();
        lvProducts = new ListView<>();
        btnRemove = new Button("Remove");
        btnRemoveAll = new Button("Remove all");
    }
    
    public Parent buildView() {
        VBox vBox = new VBox(20);
        vBox.setAlignment(Pos.CENTER);
    
        HBox hbTotalProfit = new HBox();
        hbTotalProfit.setAlignment(Pos.CENTER);
        
        HBox hbRemoveRow = new HBox(20);
        hbRemoveRow.setAlignment(Pos.CENTER);
        
        Text tTitle = new Text("Choose product to remove: ");
        tTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        Text tTotalProfitLabel = new Text("Total profit: ");
        tTotalProfitLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        tTotalProfit.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        hbTotalProfit.getChildren().addAll(tTotalProfitLabel, tTotalProfit);
        hbRemoveRow.getChildren().addAll(btnRemove, btnRemoveAll);
        vBox.getChildren().addAll(tTitle, hbTotalProfit, buildListView(), hbRemoveRow);
        
        return vBox;
    }
    
    private ListView<String> buildListView() {
        List<String> products = new ArrayList<>();
        for (Map.Entry<String, Product> entry : productMap.entrySet()) {
            products.add(String.format("ID: %s\t %s", entry.getKey(), entry.getValue().toString()));
            ids.add(entry.getKey());
        }
        ObservableList<String> obsNameList = FXCollections.observableArrayList(products);
        lvProducts.setItems(obsNameList);
        lvProducts.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        return lvProducts;
    }
    
    public Text geTTotalProfit() {
        return tTotalProfit;
    }
    
    public Button getBtnRemove() {
        return btnRemove;
    }
    
    public Button getBtnRemoveAll() {
        return btnRemoveAll;
    }
    
    public ListView<String> getLvProducts() {
        return lvProducts;
    }
}
