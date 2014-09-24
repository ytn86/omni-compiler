/*
 * $TSUKUBA_Release: $
 * $TSUKUBA_Copyright:
 *  $
 */

package exc.xcalablemp;

import java.util.HashMap;

public class XMPsymbolTable {
  private HashMap<String, XMPobject> _XMPobjectTable;
  private HashMap<String, XMPalignedArray> _XMPalignedArrayTable;
  private HashMap<String, XMPcoarray> _XMPcoarrayTable;
  private HashMap<String, XACClayoutedArray> _XACCdeviceArrayTable;

  public XMPsymbolTable() {
    _XMPobjectTable = new HashMap<String, XMPobject>();
    _XMPalignedArrayTable = new HashMap<String, XMPalignedArray>();
    _XMPcoarrayTable = new HashMap<String, XMPcoarray>();
    _XACCdeviceArrayTable = new HashMap<String, XACClayoutedArray>();
  }

  public void putXMPobject(XMPobject o) {
    _XMPobjectTable.put(o.getName(), o);
  }

  public XMPobject getXMPobject(String name) {
    return _XMPobjectTable.get(name);
  }

  public XMPnodes getXMPnodes(String name) {
    XMPobject o = _XMPobjectTable.get(name);

    if (o == null) return null;
    else if (o.getKind() != XMPobject.NODES) return null;
    else return (XMPnodes)o;
  }

  public XMPtemplate getXMPtemplate(String name) {
    XMPobject o = _XMPobjectTable.get(name);

    if (o == null) return null;
    else if (o.getKind() != XMPobject.TEMPLATE) return null;
    else return (XMPtemplate)o;
  }

  public void putXMPalignedArray(XMPalignedArray array) {
    _XMPalignedArrayTable.put(array.getName(), array);
  }

  public XMPalignedArray getXMPalignedArray(String name) {
    return _XMPalignedArrayTable.get(name);
  }

  public void putXMPcoarray(XMPcoarray array) {
    _XMPcoarrayTable.put(array.getName(), array);
  }

  public XMPcoarray getXMPcoarray(String name) {
    return _XMPcoarrayTable.get(name);
  }
  
  public void putXACCdeviceArray(XACClayoutedArray array) {
    _XACCdeviceArrayTable.put(array.getName(), array);
  }

  public XACClayoutedArray getXACCdeviceArray(String name) {
    return _XACCdeviceArrayTable.get(name);
  }
}
