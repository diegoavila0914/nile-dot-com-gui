/* Name: Diego Avila
 * Course: CNT 4714 - Fall 2024
 * Assignment title: Project 1 - An Event-driven Enterprise Simulation
 * Date: Sunday September 8, 2024
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.text.DateFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class NileDotCom extends JFrame
{
	// Variable Declarations
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static final int MAXITEMS = 5; // most items a shopping cart can hold
	private static final int MAXARRAYSIZE = 5;
	
	
	private JLabel		blankLabel, blankLabel2, blankLabel3, idLabel, qtyLabel, itemLabel, subTotalLabel, controlsLabel, cartLabel;
	private JTextField 	blankTextField, idTextField, qtyTextField, itemTextField, subTotalTextField;
	private JTextField 	cartTextField1, cartTextField2, cartTextField3, cartTextField4, cartTextField5;
	private JButton 	blankButton, searchB, confirmB, viewB, finishB, newB, exitB;
	
	
	private SearchButtonHandler 	searchbHandler;
	private	ConfirmButtonHandler	confbHandler;
	private ViewButtonHandler		viewbHandler;
	private FinishButtonHandler 	finbHandler;
	private NewButtonHandler		newbHandler;
	private ExitButtonHandler		exitbHandler;
	
	static NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
	static NumberFormat percentFormatter = NumberFormat.getPercentInstance();
	static DecimalFormat decimalFormatter = (DecimalFormat) percentFormatter;
	
	// Array Declaration
	static String [] itemIDArray;
	static String [] itemTitleArray;
	static String [] itemInStockArray;
	static String [] cartItemsArray = new String[MAXARRAYSIZE];
	static double [] itemPriceArray;
	static int 	  [] itemQuantityArray;
	static double [] itemDiscountArray;
	static double [] itemSubtotalArray;
	static int 	  [] itemFoundInFileArray;
	static JTextField[] cartLineArray;
	
	
	static String itemID = "", itemTitle="", outputStr="", maxArraySizeStr="", itempriceStr="", itemInStock="",
				  itemQuantityStr="", itemDiscountStr="", taxRateStr="", discountRateStr, orderSubtotalStr;
	
	static double itemPrice = 0, itemSubtotal=0, orderSubtotal=0, orderTotal=0, itemDiscount=0, orderTaxAmount;
	
	static int 	  itemQuantity=0, itemCount=0, itemQtyOnHand=0, fileRowCounter=0;
	
	final static double TAX_RATE = 0.060,
						DISCOUNT_FOR_05 = .10,
						DISCOUNT_FOR_10 = .15,
						DISCOUNT_FOR_15 = .20;
	
	String fileName;
	
//**************************************************************
	// constructor for GUI
	public NileDotCom() 
	{
		setTitle("Nile.Com - Fall 2024");
		setSize(WIDTH, HEIGHT);
		
		Color light_blue = new Color(51, 204, 255);
		Color light_green = new Color(0, 255, 51);
		
		// Initialize labels for the objects in north panel
		blankButton = new JButton("  ");
		blankLabel = new JLabel(" ", SwingConstants.RIGHT);
		blankLabel2 = new JLabel(" ", SwingConstants.RIGHT);
		blankLabel3 = new JLabel(" ", SwingConstants.RIGHT);
		idLabel = new JLabel("Enter item ID for Item #" + (itemCount+1) + ":", SwingConstants.RIGHT);
		qtyLabel = new JLabel("Enter quantity for Item #" + (itemCount+1) + ":", SwingConstants.RIGHT);
		itemLabel = new JLabel("Details for Item #" + (itemCount+1) + ":", SwingConstants.RIGHT);
		subTotalLabel = new JLabel("Current Subtotal for " + (itemCount) + " item(s):", SwingConstants.RIGHT);
		cartLabel = new JLabel("Your Shopping Cart Is Currentely Empty", SwingConstants.CENTER);
		controlsLabel = new JLabel(" USER CONTROLS ", SwingConstants.RIGHT);
		
		// Initialize text fields for the objects in the north panel
		blankTextField = new JTextField();
		idTextField = new JTextField();
		qtyTextField = new JTextField();
		itemTextField = new JTextField();
		subTotalTextField = new JTextField();
		
		// Initialize text fields in the center panel
		cartTextField1 = new JTextField();
		cartTextField2 = new JTextField();
		cartTextField3 = new JTextField();
		cartTextField4 = new JTextField();
		cartTextField5 = new JTextField();
		
		// construct and register process buttons and handlers in south panel 
		searchB = new JButton("Search for Item #" + (itemCount+1));
		searchbHandler = new SearchButtonHandler();
		searchB.addActionListener(searchbHandler);
		
		// construct and register confirm buttons and handlers in south panel 
		confirmB = new JButton("Add Item #" + (itemCount+1) + " To Cart");
		confirmB.setEnabled(false); //initially disabled
		confbHandler = new ConfirmButtonHandler();
		confirmB.addActionListener(confbHandler);
		
		// construct and register view buttons and handlers in south panel 
		viewB = new JButton("View Cart");
		viewB.setEnabled(false); //initially disabled
		viewbHandler = new ViewButtonHandler();
		viewB.addActionListener(viewbHandler);
		
		// construct and register finish buttons and handlers in south panel 
		finishB = new JButton("Check Out");
		finishB.setEnabled(false); //initially disabled
		finbHandler = new FinishButtonHandler();
		finishB.addActionListener(finbHandler);
		
		// construct and register new buttons and handlers in south panel 
		newB = new JButton("Empty Cart - Start A New Order");
		newbHandler = new NewButtonHandler();
		newB.addActionListener(newbHandler);
		
		// construct and register exit buttons and handlers in south panel 
		exitB = new JButton("Exit (Close App)");
		exitbHandler = new ExitButtonHandler();
		exitB.addActionListener(exitbHandler);
		
		Container pane = getContentPane();
		
		GridLayout grid6by2 = new GridLayout(6,2,8,4);
		GridLayout grid7by2 = new GridLayout(7,2,8,4);
		
		// construct panels
		JPanel northPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel southPanel = new JPanel();
		
		// set layouts
		northPanel.setLayout(grid6by2);
		centerPanel.setLayout(grid7by2);
		southPanel.setLayout(grid6by2);
		
		// add labels and text fields accordingly 
		northPanel.add(blankLabel);
		northPanel.add(blankLabel2);
		northPanel.add(idLabel);
		idLabel.setForeground(Color.yellow);
		northPanel.add(idTextField);
		northPanel.add(qtyLabel);
		qtyLabel.setForeground(Color.yellow);
		northPanel.add(qtyTextField);
		northPanel.add(itemLabel);
		itemLabel.setForeground(Color.red);
		northPanel.add(itemTextField);
		northPanel.add(subTotalLabel);
		subTotalLabel.setForeground(Color.cyan);
		northPanel.add(subTotalTextField);
		centerPanel.add(cartLabel);
		cartLabel.setForeground(Color.red);
		centerPanel.add(cartTextField1);
		centerPanel.add(cartTextField2);
		centerPanel.add(cartTextField3);
		centerPanel.add(cartTextField4);
		centerPanel.add(cartTextField5);
		southPanel.add(controlsLabel);
		controlsLabel.setForeground(Color.white);
		controlsLabel.setHorizontalAlignment(JLabel.CENTER);
		
		// add buttons
		southPanel.add(blankLabel3);
		southPanel.add(searchB);
		southPanel.add(confirmB);
		southPanel.add(viewB);
		southPanel.add(finishB);
		southPanel.add(newB);
		southPanel.add(exitB);
		
		// add panes and set backgrounds for panels
		pane.add(northPanel, BorderLayout.NORTH);
		pane.add(centerPanel, BorderLayout.CENTER);
		pane.add(southPanel, BorderLayout.SOUTH);
		centerFrame(WIDTH, HEIGHT);
		pane.setBackground(Color.DARK_GRAY);
		northPanel.setBackground(Color.DARK_GRAY);
		centerPanel.setBackground(Color.LIGHT_GRAY);
		southPanel.setBackground(Color.BLUE);
		
	}

//**************************************************************
	public void centerFrame(int widthOfFrame, int heightOfFrame) {
		
		Toolkit aToolkit = Toolkit.getDefaultToolkit();
		
		Dimension screen = aToolkit.getScreenSize();
		
		int xPositionOfFrame = (screen.width - widthOfFrame) / 2;
		int yPositionOfFrame = (screen.height - heightOfFrame) / 2;
		
		setBounds(xPositionOfFrame, yPositionOfFrame, widthOfFrame, heightOfFrame);
		
	}

//**************************************************************
	private class SearchButtonHandler implements ActionListener 
	{
		
		public void actionPerformed(ActionEvent e) 
		{
			//System.outprintin("The Find Item Button Was CLicked..."); //debug statement
			try
			{
				// Set up error messages
				String zeroQtyError = "Invalid Quantity Entered";
				//String itemNotFoundError = "Item not in file";
				//String insuffStockError = "Insufficient stock. Please reduce the quantity";
				
				// Get user input from the GUI fields
				String inputItemID = idTextField.getText();
				String inputQuantityStr = qtyTextField.getText(); 
				
				// Set up for the file reading
				BufferedReader reader = new BufferedReader(new FileReader("inventory.csv"));
				String line;
				boolean itemFound = false;
				
				// Loop through the file until itemID found or EOF reached
				while((line = reader.readLine()) != null) {
					String[] itemData = line.split(",\\s");
					
					String fileItemID = itemData[0].trim();  // Item ID (String)
					String fileItemTitle = itemData[1].replace("\"", "").trim();  // Item Description (String, remove quotes)
					boolean fileInStock = Boolean.parseBoolean(itemData[2].trim());  // Availability (boolean)
					int fileQuantityInStock = Integer.parseInt(itemData[3].trim());  // Quantity (int)
					double fileItemPrice = Double.parseDouble(itemData[4].trim());  // Price (double)
					
					// If itemID found
					if(fileItemID.equals(inputItemID)) {
						if(!fileInStock || fileQuantityInStock == 0) {
							JOptionPane.showMessageDialog(null, "Sorry.. that item is out of stock, please try another item");
							idTextField.setText("");
							qtyTextField.setText("");
							idTextField.requestFocus();
							confirmB.setEnabled(false);
							return;
						}
						// Put the values from the file line into the placeholders
						itemID = fileItemID;
						itemTitle = fileItemTitle;
						itemPrice = fileItemPrice;
						itemQtyOnHand = fileQuantityInStock;
						
						// Indicate item found
						itemFound = true;
						int enteredQuantity = Integer.parseInt(inputQuantityStr);
						itemQuantityStr = String.valueOf(enteredQuantity);
						
						// Check if the item is in stock and has sufficient quantity
						if(fileInStock && itemQtyOnHand > 0 && enteredQuantity <= itemQtyOnHand) {
							
							// Calculate discount based on the quantity
							if (enteredQuantity >= 15) {
								itemDiscount = DISCOUNT_FOR_15;
							} else if (enteredQuantity >= 10) {
								itemDiscount = DISCOUNT_FOR_10;
							} else if (enteredQuantity >= 5) {
								itemDiscount = DISCOUNT_FOR_05;
							} else {
								itemDiscount = 0;
							}
							
							// Calculate item subtotal after applying discount
							double discountedPrice = itemPrice * (1 - itemDiscount);
							itemSubtotal = discountedPrice * enteredQuantity;
							
							String formattedPrice = currencyFormatter.format(itemPrice);
		                    String formattedDiscount = percentFormatter.format(itemDiscount);
		                    String formattedSubtotal = currencyFormatter.format(itemSubtotal);
		                    
		                    itemLabel.setText("Details for Item #" + (itemCount+1) + ":");
		                    itemTextField.setText(itemID + " \"" + itemTitle + "\" " + formattedPrice + " " + enteredQuantity 
		                            + " " + formattedDiscount + " " + formattedSubtotal);
							
							// Enable confirm button and disable search button
							confirmB.setEnabled(true);
							searchB.setEnabled(false);
						} else if (enteredQuantity > itemQtyOnHand) {
							JOptionPane.showMessageDialog(null, "Insufficient stock. Only " + fileQuantityInStock + " on hand. "
									+ "Please reduce the quantity", "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
							qtyTextField.setText("");
							confirmB.setEnabled(false);
						} else {
							JOptionPane.showMessageDialog(null, zeroQtyError);
							confirmB.setEnabled(false);
						}
						break;
					}
				}

				// If item was not found
				if(!itemFound) {
					JOptionPane.showMessageDialog(null, "Item ID " + inputItemID + " not in file", "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
					idTextField.setText("");
					qtyTextField.setText("");
					confirmB.setEnabled(false);
				}
			} catch(FileNotFoundException fileNotFoundException) {
				System.out.println("Error: Inventory file not found");
			} catch(IOException ioException) {
				System.out.println("Error reading from inventory file.");
			} catch(NumberFormatException numFormatExcpetion) {
				System.out.println("Invalid number format");
			}
		}

		
	} // end class
	
//**************************************************************
	private class ConfirmButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			try {
	            if (itemCount < 5) { // Check that we haven't exceeded the cart limit
	                // Add the item details from the last search to the appropriate cart text field
	                String cartItemDetails = "Item " + (itemCount + 1) + " - SKU: " + itemID + ", Desc: \"" + itemTitle + "\", Price Ea: " + currencyFormatter.format(itemPrice) + 
	                                        ", Qty: " + itemQuantityStr + ", Total: " + currencyFormatter.format(itemSubtotal);
	                
	                // Add the current item to the appropriate cart text field
	                switch(itemCount) {
	                    case 0:
	                        cartTextField1.setText(cartItemDetails);
	                        break;
	                    case 1:
	                        cartTextField2.setText(cartItemDetails);
	                        break;
	                    case 2:
	                        cartTextField3.setText(cartItemDetails);
	                        break;
	                    case 3:
	                        cartTextField4.setText(cartItemDetails);
	                        break;
	                    case 4:
	                        cartTextField5.setText(cartItemDetails);
	                        break;
	                }

	                // Update the running total for the cart
	                orderSubtotal += itemSubtotal;
	                subTotalTextField.setText(currencyFormatter.format(orderSubtotal));
	                subTotalLabel.setText("Current Subtotal for " + (itemCount + 1) + " item(s):");

	                // Increment the item counter
	                itemCount++;
	                //Updates status of shopping cart
	                if (itemCount == 1) {
	                    cartLabel.setText("Your Shopping Cart Currently Contains 1 item(s)");
	                } else {
	                    cartLabel.setText("Your Shopping Cart Currently Contains " + itemCount + " item(s)");
	                }
	                
	                // Clear input fields for next input
	                idTextField.setText("");
	                qtyTextField.setText("");

	                if(itemCount < 5) {
	                	// Update the labels to reflect the correct item number
		                idLabel.setText("Enter item ID for Item #" + (itemCount + 1)+ ":");
		                qtyLabel.setText("Enter quantity for Item #" + (itemCount + 1)+ ":");                
		                // Update buttons for the next item search
		                searchB.setText("Search For Item #" + (itemCount + 1));
		                confirmB.setText("Add Item #" + (itemCount + 1) + " To Cart");	                
		                // Disable the confirm button and enable the search button again
		                confirmB.setEnabled(false);
		                searchB.setEnabled(true);
		                viewB.setEnabled(true);
		                finishB.setEnabled(true);
	                }else {
	                	JOptionPane.showMessageDialog(null, "The cart is full! Please check out or empty the cart.");
	                	idTextField.setEditable(false);
	    	            qtyTextField.setEditable(false);
	                    searchB.setEnabled(false);
	                    confirmB.setEnabled(false);
	                }
	                

	                // Focus back on the ID text field for the next search
	                idTextField.requestFocus();
	                
	            } else {
	                // Display message if the cart is full (i.e., 5 items)
	                JOptionPane.showMessageDialog(null, "The cart is full! Please check out or empty the cart.");
	                idTextField.setEditable(false);
    	            qtyTextField.setEditable(false);
	                searchB.setEnabled(false);
	                confirmB.setEnabled(false);
	            }
	        } catch (Exception ex) {
	            System.out.println("Error in Confirm Button Handler: " + ex.getMessage());
	        }
		}
		
	}
	
//**************************************************************	
	private class ViewButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			//System.out.println("The View Cart Button Was Clicked...");
			 StringBuilder cartDetails = new StringBuilder();
			 	try {
		        // Check if any cart text fields contain text and add them to the StringBuilder
		        if (!cartTextField1.getText().isEmpty()) 
		        	cartDetails.append(cartTextField1.getText() + "\n");
		        if (!cartTextField2.getText().isEmpty()) 
		        	cartDetails.append(cartTextField2.getText() + "\n");
		        if (!cartTextField3.getText().isEmpty()) 
		        	cartDetails.append(cartTextField3.getText() + "\n");
		        if (!cartTextField4.getText().isEmpty()) 
		        	cartDetails.append(cartTextField4.getText() + "\n");
		        if (!cartTextField5.getText().isEmpty()) 
		        	cartDetails.append(cartTextField5.getText() + "\n");

		        // Show a message dialog with the collected cart details
		        if (cartDetails.length() > 0) {
		            JOptionPane.showMessageDialog(null, cartDetails.toString(), "Nile Dot Com - Current Shopping Cart Status", JOptionPane.INFORMATION_MESSAGE);
		        } else {
		            JOptionPane.showMessageDialog(null, "Your cart is empty.", "Current Order", JOptionPane.INFORMATION_MESSAGE);
		        }
		        
			 	} catch(Exception ex) {
			            ex.printStackTrace();
			            JOptionPane.showMessageDialog(null, "Error encountered: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			 	}
			
		}
	}

//**************************************************************
	private class FinishButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			try {
				//System.out.println("The Check Out Button Was Clicked..."); //debug
				// set class variables
				int nonEmptyTextFields = 0;
				orderTaxAmount = orderSubtotal * TAX_RATE;
				orderTotal = orderSubtotal + orderTaxAmount;
		
	            // Message Dialog and transcation.csv formatted date 
				SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM d, yyyy, h:mm:ss a 'EDT'");
	            displayFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
	            String formattedDate = displayFormat.format(new Date());  // Use format() on a new Date object
	            
	            // transactionsId format date and time for csv
	            SimpleDateFormat transactionIdFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
	            String transactionId = transactionIdFormat.format(new Date());
	            
				// set up a StringBuilder object to hold the big string of data for the invoice
				StringBuilder invoice = new StringBuilder();
				JTextField[] cartFields = {cartTextField1, cartTextField2, cartTextField3, cartTextField4, cartTextField5};
				
		        // Loop through text fields and count non-empty ones
				for (JTextField field : cartFields) {
		            if (!field.getText().isEmpty()) {
		                nonEmptyTextFields++;
		            }
		        }
				// start building the invoice
	            invoice.append("Date: ").append(formattedDate).append("\n\n");
	            invoice.append("Number of line items: ").append(nonEmptyTextFields).append("\n\n");
	            invoice.append("Item# / ID / Title / Price / Qty / Disc % / Subtotal:\n\n");
					
				// build up the output message by just appending strings together
	            // Count non-empty text fields for number of line items
	            int lineItemCount = 0;
	            // set up the file writer object
	            FileWriter transactionFile = new FileWriter("transactions.csv", true);
	            StringBuilder csvLines = new StringBuilder();

	            for (JTextField field : cartFields) {
	                if (!field.getText().isEmpty()) {
	                    lineItemCount++;
	                    invoice.append(lineItemCount).append(". ").append(field.getText()).append("\n");

	                    // Assume each field's text is formatted correctly for the CSV
	                    csvLines.append(transactionId).append(", ").append(field.getText()).append(", ").append(formattedDate).append("\n");
	                }
	            }
				    
				// write lines to the transactions.csv file
	            transactionFile.write(csvLines.toString());
	            transactionFile.close();
			
				// dump dialog box with final invoice
	            invoice.append("\n\n\nOrder subtotal: $").append(String.format("%.2f", orderSubtotal)).append("\n");
	            invoice.append("\nTax rate: ").append((int) (TAX_RATE * 100)).append("%\n");
	            invoice.append("\nTax amount: $").append(String.format("%.2f", orderTaxAmount)).append("\n");
	            invoice.append("\nORDER TOTAL: $").append(String.format("%.2f", orderTotal)).append("\n\n");
	            invoice.append("Thanks for shopping at Nile Dot Com!");
	            // Display the final invoice
	            JOptionPane.showMessageDialog(null, invoice.toString(), "Nile Dot Com - FINAL INVOICE", JOptionPane.INFORMATION_MESSAGE);
	
				/* reset buttons and fields in GUI
	            for (JTextField field : cartFields) {
	                field.setText("");
	            }*/
	            //idTextField.setEditable(false);
	            //qtyTextField.setEditable(false);
	            //subTotalTextField.setText("");
	            searchB.setEnabled(false);
	            confirmB.setEnabled(false);
	            finishB.setEnabled(false);
			} //end try
			catch (IOException ex) {
	            JOptionPane.showMessageDialog(null, "Error writing to the transaction file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        } catch (Exception ex) {
	            JOptionPane.showMessageDialog(null, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        }	
		}
	}

//**************************************************************
	private class NewButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			//System.out.println("The Empty Cart Button Was Clicked...");
			 
			// Reset all cart text fields
	        cartTextField1.setText("");
	        cartTextField2.setText("");
	        cartTextField3.setText("");
	        cartTextField4.setText("");
	        cartTextField5.setText("");

	        // Reset the cart label to indicate the cart is empty
	        cartLabel.setText("Your Shopping Cart Is Currently Empty");

	        // Reset global variables
	        itemCount = 0;
	        orderSubtotal = 0.0;

	        // Reset subtotal display
	        idLabel.setText("Enter item ID for item #1:");
	        qtyLabel.setText("Enter quantity for item #1:");
	        itemLabel.setText("Details for Item #1:");
	        subTotalLabel.setText("Current Subtotal for 0 items:");

	        // Clear item ID and quantity fields
	        idTextField.setText("");
	        qtyTextField.setText("");
	        itemTextField.setText("");
	        subTotalTextField.setText("");
	        searchB.setText("Search For Item #1");
	        confirmB.setText("Add Item #1 To Cart");

	        // Reset and enable the search button for a new order
	        idTextField.setEnabled(true);
	        qtyTextField.setEnabled(true);
	        searchB.setEnabled(true);
	        confirmB.setEnabled(false); // Disable confirm button until next item is searched
	        viewB.setEnabled(false); // Disable view button until items are added
	        finishB.setEnabled(false); // Disable finish button until items are added

	        // Optionally, you might also want to reset any other state-specific controls

	        // Focus back on the ID text field to start a new order
	        idTextField.requestFocus();
			
		}
		
	}
	
//**************************************************************
	private class ExitButtonHandler	implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			//System.out.println("The Exit Button Was Clicked...");
			System.exit(0);
			
		}
		
	} 
	public static void main(String[] args) 
	{
		// create instance
		JFrame aNewStore = new NileDotCom();
		aNewStore.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		aNewStore.setVisible(true); 

	}

}
