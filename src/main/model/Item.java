package main.model;

import com.sun.istack.internal.NotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Formatter;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.FIELD)
public class Item implements Serializable {

    private UUID id;
    private String name;
    private int count;
    private double price;
    private Date dateOfReceipt;


    //==================================================check methods=========================================================
    private static void checkPrice(double price) throws IllegalArgumentException {
        if (price < 0)
            throw new IllegalArgumentException(String.format("Negative price: %f ", price));
    }

    private static void checkCount(int count) throws IllegalArgumentException {
        if (count < 1)
            throw new IllegalArgumentException(String.format("invalid count of products: %d ", count));
    }

    private static void checkDateOfReceipt(Date dateOfReceipt) throws IllegalArgumentException {
        if (dateOfReceipt.before(new Date()))
            throw new IllegalArgumentException(String.format("invalid date of receipt: %s ", dateOfReceipt.toString()));
    }

    private static void check(String name, double price, int count, Date dateOfReceipt) throws IllegalArgumentException {
        checkPrice(price);
        checkCount(count);
//        checkDateOfReceipt(dateOfReceipt);
    }

//==================================================Constructor=========================================================

    /**
     * пустой конструктор для пустого объекта
     */
    public Item() {
    }


    public Item(@NotNull String name, double price, int count, @NotNull Date dateOfReceipt) throws IllegalArgumentException {
        check(name, price, count, dateOfReceipt);
        this.name = name;
        this.price = price;
        this.count = count;
        this.dateOfReceipt = dateOfReceipt;
        this.id = UUID.randomUUID();
    }

    public Item(@NotNull UUID uuid, @NotNull String name, double price, int count, @NotNull Date dateOfReceipt) throws IllegalArgumentException {
        check(name, price, count, dateOfReceipt);
        this.name = name;
        this.price = price;
        this.count = count;
        this.dateOfReceipt = dateOfReceipt;
        this.id = uuid;
    }
//==============================================Getters=and=Setters=====================================================


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) throws IllegalArgumentException {
        checkPrice(price);
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) throws IllegalArgumentException {
        checkCount(count);
        this.count = count;
    }

    public Date getDateOfReceipt() {
        return dateOfReceipt;
    }

    /*"""
        It use the method toStringToFile to write in file(format). And plus '\n'
        For data format is standart of DateFormat.getInstance()
        Recomended use "\n" separator
    """*/
    public static void writeItem(@NotNull Writer out, @NotNull Item item, @NotNull char separator) throws IOException {
        out.write(item.toStringToFile() + separator);
    }

//================================================Object=Method=========================================================

    @Override
    public int hashCode() {
        int hash = name.hashCode();
        long f = Double.doubleToLongBits(price);

        hash += (int) f + (int) (f >>> 32);
        hash += count;
        hash += dateOfReceipt.hashCode();

        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Item))
            return false;
        Item item = (Item) obj;
        if (item.count != this.count)
            return false;
        if (Math.abs(item.price - this.price) > Double.MIN_NORMAL)
            return false;
        if (!item.name.equals(this.name))
            return false;
        return item.dateOfReceipt.equals(dateOfReceipt);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Item ans = new Item();
        ans.name = this.name;
        ans.count = this.count;
        ans.price = this.price;
        ans.dateOfReceipt = this.dateOfReceipt;
        return ans;
    }

    /*"""
        It use the method toStringToFile to read in file(format).  And plus '\n'
        For data format is standart of DateFormat.getInstance()
    """*/
    public static Item readItem(@NotNull Reader in, @NotNull char separator) throws IOException, ParseException {
        Item ans = null;

        DateFormat df = DateFormat.getDateInstance();

        StringBuilder temp = new StringBuilder();
        char curChar;
        while ((curChar = (char) in.read()) != separator) {
            temp.append(curChar);
        }
        String[] data = temp.toString().split(" , ");
        int count = Integer.parseInt(data[2]);
        double price = Double.parseDouble(data[3]);
        Date date = df.parse(data[4]);

        ans = new Item();
        ans.name = data[1];
        ans.count = count;
        ans.price = price;
        ans.dateOfReceipt = date;
        ans.id = UUID.fromString(data[0]);

        return ans;
    }

    public void setDateOfReceipt(Date dateOfReceipt) {
//        checkDateOfReceipt(dateOfReceipt);
        this.dateOfReceipt = dateOfReceipt;
    }

//==========================================Static=Read=Write=Method====================================================

    @Override
    public String toString() {
        Formatter ans = new Formatter();
        ans.format("Item id: %s ", id.toString());
        ans.format("name: %s ", name);
        ans.format("count: %d ", count);
        ans.format("price: %f ", price);
        ans.format("date of receipt: %s", dateOfReceipt.toString());
        return ans.toString();
    }

    public String toStringToFile() {
        DateFormat df = DateFormat.getDateInstance();
        return this.id.toString()+ " , " + this.getName() + " , " +
                this.getCount() + " , " +
                this.getPrice() + " , " +
                df.format(this.getDateOfReceipt());
    }
}
