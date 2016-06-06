package io.lightlink.excel;

import io.lightlink.utils.Utils;
import jdk.nashorn.internal.objects.NativeDate;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WritingExcelStreamVisitor extends AbstractExcelStreamVisitor {

    public static final Logger LOG = LoggerFactory.getLogger(WritingExcelStreamVisitor.class);

    private Map<String, Object> data = new HashMap<String, Object>();
    private DateFormat dateFormat;

    private Iterator currentRowIterator;
    private String currentRowProperty;

    public WritingExcelStreamVisitor(Map<String, Object> data, DateFormat dateFormat) {
        this.data.putAll(data);
        this.dateFormat = dateFormat;
    }

    @Override
    protected void nextRow() {
        if (currentRowIterator != null && currentRowIterator.hasNext())
            data.put(currentRowProperty, currentRowIterator.next());
    }

    @Override
    protected int getRowRepeatCount(RowNode rowNode) {
        List<CellNode> cells = rowNode.getCells();

        if (cells.size() > 0 && cells.get(0).getDecodedValue() != null) {
            Matcher m = Pattern.compile("^:[a-zA-Z_0-9\\.]+\\[\\]\\.").matcher(cells.get(0).getDecodedValue());
            if (m.find()) {
                String s = m.group(0);
                s = s.substring(1, s.length() - 1);

                Object value = getPropertyValue(data, s.substring(0, s.length() - 2));
                if (value != null) {
                    if (value instanceof Object[])
                        value = Arrays.asList((Object[]) value);
                    if (value instanceof Collection) {
                        Collection coll = (Collection) value;
                        currentRowIterator = coll.iterator();
                        currentRowProperty = s;
                        return coll.size();
                    }
                }
            }
        }

        return 1;
    }

    protected void handleBinding(String property, RowNode rowNode, int i, RowPrintCallback rowPrintCallback) {

        while (property.endsWith("[]"))               // cut ending [][]
            property = property.substring(0, property.length() - 2);

        List<CellNode> cells = rowNode.getCells();
        CellNode cell = cells.get(i);

        Object value = getPropertyValue(data, property);

        if (value == null) {
            cell.changeValue("");
            return;
        }

        if (value instanceof Number) {
            cell.changeValue((Number) value);
            return;
        }

        if (value instanceof NativeDate) {
            value = new Date((long)NativeDate.getTime(value));
        }
        if (value instanceof Date && dateFormat!=null) {
            value = dateFormat.format((Date) value);
        }


        value = Utils.tryConvertToJavaCollections(value);

        if (value instanceof Map) {
            value = Collections.singletonList(value);
        }

        if (value instanceof List) {
            List list = (List) value;

            cells.remove(i);

            if (list.isEmpty())
                return;

            Object mayBeCollection = Utils.tryConvertToJavaCollections(list.get(0));
            if (!(mayBeCollection instanceof Map) && !(mayBeCollection instanceof List)) {
                list = Collections.singletonList(list); // one dimentional array as 2 dimention of one row
            }
            for (int l = 0; l < list.size(); l++) {
                Object line = list.get(l);
                if (line instanceof Map)
                    line = ((Map) line).values();

                line = Utils.tryConvertToJavaCollections(line);

                if (line instanceof Collection) {
                    Collection lineList = (Collection) line;
                    if (l == 0) { // first line
                        for (int j = 0; j < lineList.size(); j++) {
                            CellNode cc = cell.clone();
                            cells.add(j + i, cc);
                            cc.changeValue("");
                        }
                    }
                    int j = 0;
                    for (Iterator iterator = lineList.iterator(); iterator.hasNext(); j++) {
                        Object propertyValue = iterator.next();

                        CellNode cc = cells.get(i + j);

                        if (propertyValue == null) {
                            propertyValue = "";
                        }
                        if (propertyValue instanceof Date) {
                            propertyValue = dateFormat.format((Date) value);
                        }
                        if (propertyValue instanceof Number)
                            cc.changeValue((Number) propertyValue);
                        else
                            cc.changeValue(propertyValue.toString());
                    }

                    if (l < list.size() - 1)
                        rowPrintCallback.printRowNode(rowNode);

                }

            }


        } else {

            cell.changeValue(value.toString());
        }
    }

    private Object getPropertyValue(Object data, String property) {

        if (data instanceof Map && ((Map) data).containsKey(property))
            return ((Map) data).get(property);

        int pos = property.indexOf("[].");
        if (pos!=-1){
            String containerProperty = property.substring(0,pos+2);
            String subProperty = property.substring(pos+3);
            Object containerObj = getPropertyValue(data, containerProperty);
            return getPropertyValue(containerObj,subProperty);
        }

        try {
            return PropertyUtils.getProperty(data, property);
        } catch (Exception e) {
            LOG.error(e.toString(), e);
            return null;
        }

    }

}
