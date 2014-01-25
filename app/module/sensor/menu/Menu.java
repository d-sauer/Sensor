package module.sensor.menu;

import java.util.LinkedList;
import java.util.List;

import play.api.mvc.Call;
import play.mvc.Controller;

public class Menu {

    private List<Menu> subMenu = new LinkedList<Menu>();

    private String caption;
    private String title;
    private Call link;
    private boolean expand = false;
    private Class controller;
    private String module;
    private String method = "loadModule";
    private Object[] methodParams;

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setModule(Class<? extends Controller> controller) {
        this.controller = controller;
        this.module = controller.getName();
    }

    public void setModule(Class<? extends Controller> controller, String method) {
        this.controller = controller;
        this.module = controller.getName();
        this.method = method;
    }

    public void setModule(Class<? extends Controller> controller, String... params) {
        this.controller = controller;
        this.module = controller.getName();
        this.methodParams = params;
    }

    public void setModule(Class<? extends Controller> controller, String method, Object ... params) {
        this.controller = controller;
        this.module = controller.getCanonicalName();
        this.method = method;
        this.methodParams = params;
    }

    public String getCall() {
        StringBuilder sb = new StringBuilder();
        String module = getModule();

        if (module != null) {
            sb.append(getModule());
            sb.append("." + getMethod());

            Object[] params = getMethodParams();
            if (params != null && params.length != 0) {
                sb.append("(");
                for (int p = 0; p < params.length; p++) {
                    if (p != 0)
                        sb.append(",");

                    sb.append(params[p].toString());
                }
                sb.append(")");
            } else {
                sb.append("()");
            }
            
            return sb.toString();
        } else {
            return null;
        }
    }

    public String getModule() {
        return module;
    }

    public String getMethod() {
        return method;
    }

    public Object[] getMethodParams() {
        return methodParams;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Call getLink() {
        return link;
    }

    public void setLink(Call link) {
        this.link = link;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public Menu() {

    }

    public Menu(Menu m) {
        m.addSubmenu(this);
    }

    public Menu(String caption, Call link) {
        this.caption = caption;
        this.link = link;
    }

    public Menu newSubmenuItem() {
        Menu me = new Menu();
        subMenu.add(me);
        return me;
    }

    public void addSubmenu(Menu me) {
        subMenu.add(me);
    }

    public Menu addSubmenu(String caption, Call link) {
        Menu me = new Menu(caption, link);
        subMenu.add(me);
        return me;
    }

    public boolean isRoot() {
        if (caption == null)
            return true;
        else
            return false;
    }

    public boolean hasSubmenu() {
        if (subMenu != null && subMenu.size() != 0)
            return true;
        else
            return false;
    }

    public List<Menu> getMenuElements() {
        return subMenu;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isRoot() == true) {
            for (Menu m : subMenu) {
                sb.append(m.toString());
            }
        } else {
            sb.append((getCaption() != null ? getCaption() : "-null-") + "\n");

            for (Menu m : subMenu) {
                sb.append("  " + m.toString());
            }
        }

        return sb.toString();
    }

}
