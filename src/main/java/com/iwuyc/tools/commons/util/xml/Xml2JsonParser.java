package com.iwuyc.tools.commons.util.xml;

import com.google.gson.stream.JsonWriter;
import com.iwuyc.tools.commons.util.NumberUtils;
import com.iwuyc.tools.commons.util.string.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Branch;
import org.dom4j.CharacterData;
import org.dom4j.Element;
import org.dom4j.Node;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Xml 解析器
 *
 * @author Neil
 */
@Slf4j
public class Xml2JsonParser implements Parser<Node, String> {
    private static final Set<Short> SIMPLE_NODE_TYPE = new HashSet<>();
    private static final Set<Short> MULTI_NODE_TYPE = new HashSet<>();

    static {
        List<Short> simpleNodeType = Arrays.asList(Node.TEXT_NODE, Node.CDATA_SECTION_NODE, Node.COMMENT_NODE);
        SIMPLE_NODE_TYPE.addAll(simpleNodeType);

        List<Short> multiNodeType = Arrays.asList(Node.DOCUMENT_NODE, Node.ELEMENT_NODE);
        MULTI_NODE_TYPE.addAll(multiNodeType);
    }

    private final Stack<?> flag = new Stack<>();

    public Xml2JsonParser() {
    }

    @Override
    public String parser(Node ele) {
        try (StringWriter write = new StringWriter(); JsonWriter jsonBuilder = new JsonWriter(write)) {
            Element root = ele.getDocument().getRootElement();
            String rootName = root.getName();
            boolean isRootNamed = "root".equalsIgnoreCase(rootName);
            boolean isArray = isArray(rootName);
            boolean isSimple = isSimpleNode(root);
            if (!isRootNamed) {
                jsonBuilder.beginObject();
                jsonBuilder.name(rootName);
                if (isArray) {
                    jsonBuilder.beginArray();
                } else if (!isSimple) {
                    jsonBuilder.beginObject();
                }
            } else if (!isSimpleNode(ele)) {
                if (isArray) {
                    jsonBuilder.beginArray();
                } else {
                    jsonBuilder.beginObject();
                }
            }
            List<Node> rootChildren = root.content();

            cleanTextNode(rootChildren);
            for (Node item : rootChildren) {
                parserEle(jsonBuilder, item);
            }
            if (!isRootNamed) {
                if (isArray) {
                    jsonBuilder.endArray();
                } else if (!isSimple) {
                    jsonBuilder.endObject();
                }
                jsonBuilder.endObject();

            } else if (!isSimpleNode(ele)) {
                if (isArray) {
                    jsonBuilder.endArray();
                } else {
                    jsonBuilder.endObject();
                }
            }
            return write.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void cleanTextNode(Collection<Node> nodes) {
        if (!shouldBeClean(nodes)) {
            return;
        }
        nodes.removeIf(item -> item instanceof CharacterData);
    }

    private boolean shouldBeClean(Collection<Node> nodes) {
        // 判断是否存在非 CharacterData 类型的节点 并且是复合类型的，如果存在，则需要将nodes中所有的 CharacterData 节点删除
        long count = nodes.parallelStream()
                .filter(item -> !(item instanceof CharacterData) && MULTI_NODE_TYPE.contains(item.getNodeType())).count();
        return count != 0;
    }

    private void parserEle(JsonWriter jsonBuilder, Node ele) throws IOException {
        if (isSimpleNode(ele)) {
            leaf(jsonBuilder, ele);
        } else if (isMultiNode(ele)) {
            multiObject(jsonBuilder, (Branch) ele);
        }
    }

    private boolean isMultiNode(Node ele) {
        return MULTI_NODE_TYPE.contains(ele.getNodeType());
    }

    private boolean isSimpleNode(Node ele) {
        short nodeType = ele.getNodeType();
        switch (nodeType) {
            case Node.ELEMENT_NODE:
            case Node.DOCUMENT_NODE:
                List<Node> children = ((Branch) ele).content();
                if (children.size() == 1) {
                    Node child = children.get(0);
                    short childType = child.getNodeType();
                    return SIMPLE_NODE_TYPE.contains(childType);
                }
                break;
            case Node.TEXT_NODE:
                return true;
            default:
                break;
        }
        return false;
    }

    private void multiObject(JsonWriter jsonBuilder, Branch ele) throws IOException {
        if (!ele.hasContent()) {
            return;
        }
        String name = ele.getName();

        jsonBuilder.name(name);
        boolean isArray = isArray(name);
        if (isArray) {
            arrayParser(jsonBuilder, ele);
            return;
        }

        List<Node> eles = ele.content();
        cleanTextNode(eles);
        jsonBuilder.beginObject();
        for (Node item : eles) {
            parserEle(jsonBuilder, item);
        }
        jsonBuilder.endObject();
    }

    private void arrayParser(JsonWriter jsonBuilder, Branch ele) throws IOException {
        jsonBuilder.beginArray();
        List<Node> eles = ele.content();
        cleanTextNode(eles);
        for (Node item : eles) {
            boolean isMultiNode = MULTI_NODE_TYPE.contains(item.getNodeType());
            if (isMultiNode) {
                jsonBuilder.beginObject();
            }
            parserEle(jsonBuilder, item);
            if (isMultiNode) {
                jsonBuilder.endObject();
            }
        }
        jsonBuilder.endArray();
    }

    private boolean isArray(String name) {
        String innerName = name.toLowerCase();
        Pattern isArr = RegexUtils.getPattern("(arr|list|array|collection)+");
        return isArr.matcher(innerName).find();
    }

    private void leaf(JsonWriter jsonBuilder, Node ele) throws IOException {
        boolean isMultiLeaf = MULTI_NODE_TYPE.contains(ele.getNodeType());
        if (isMultiLeaf) {
            jsonBuilder.name(ele.getName());
            ele = ((Branch) ele).content().get(0);
        }
        String val = ele.getStringValue();
        if (NumberUtils.isNumber(val)) {
            jsonBuilder.value(numberConvert(val));
        } else {
            jsonBuilder.value(val);
        }
    }

    private Number numberConvert(String numberStr) {
        if (NumberUtils.isInteger(numberStr)) {
            return new BigInteger(numberStr);
        } else {
            return new BigDecimal(numberStr);
        }

    }

    enum JsonFlag {
        Object_Begin,
        Object_End,
        Array_Begin,
        Array_End
    }
}
