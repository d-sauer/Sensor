package util.Http;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import play.mvc.Controller;
import play.mvc.Result;
import util.hlib.HDataContainer;
import util.logger.log;

public abstract class CallController extends Controller {

    public static final String METHOD_PREFIX = "ajax_";
    
    public static Result call(String method) throws Exception {
        return call(null, method);
    }

    /**
     * call method with prefix {@value #METHOD_PREFIX}_methodName.
     * If method not find, try with 'methodName'
     * 
     * @param cController
     * @param method
     * @return
     * @throws Exception
     */
    public static Result call(Class cController, String method) throws Exception {
        Result result = null;
        Method oMethod = null;
        Method[] methods = null;
        
        // Check if method is called with params
        JsonNode reqData = getCallJson();

        if (cController==null) {
            JsonNode jsModule = reqData.get("java_module");
            if (jsModule!=null) {
                String javaModule = jsModule.asText();
                cController = Class.forName(javaModule);
            }
            
        }
        
        if(cController==null)
            throw new Exception("Module class is not defined");
        
        methods = cController.getDeclaredMethods();
        
        
        List<String> params = null;
        Object[] mapParams = null;
        if (reqData != null) {
            JsonNode jsonParams = reqData.get("params");
            if (jsonParams!=null) {
                Iterator<JsonNode> itPar = jsonParams.getElements();
                while (itPar.hasNext()) {
                    if (params == null)
                        params = new LinkedList<String>();
    
                    JsonNode p = itPar.next();
                    params.add(p.asText());
                }
            }
        }

        // find method <method>
        boolean isMethodNameExist = false;
        for (Method m : methods) {
            if (m.getName().equals(method)) {
                isMethodNameExist = true;
                // check method arguments
                if (params != null) {
                    mapParams = mapMethodParams(m, params);

                    if (mapParams == null)
                        continue;
                }

                oMethod = m;
                break;
            }
        }

        // find method with prefix ajax_<method>
        if (oMethod == null) {
            for (Method m : methods) {
                if (m.getName().equals(METHOD_PREFIX + method)) {
                    isMethodNameExist = true;
                    // check method arguments
                    if (params != null) {
                        mapParams = mapMethodParams(m, params);

                        if (mapParams == null)
                            continue;
                    }

                    oMethod = m;
                    break;
                }
            }
        }

        // ---
        if (oMethod != null) {
            log.info("call: " + cController.getName() + "." + oMethod.getName() + (params != null && params.size() != 0 ? params.toString().replace("[", "(").replace("]", ")") : "") + " :: " + oMethod.toString());

            if (params != null) {
                result = (Result) oMethod.invoke(null, mapParams);
            } else
                result = (Result) oMethod.invoke((Object) null, (Object[]) null);

            log.info("...called: " + cController.getName() + "." + oMethod.getName() + (params != null && params.size() != 0 ? params.toString().replace("[", "(").replace("]", ")") : "") + " :: " + oMethod.toString());
        } else {
            if (isMethodNameExist==false)
                throw new Exception("Method " + method + " not found in class " + cController.getName());
            else if (isMethodNameExist==true && params.size()!=0)
                throw new Exception("Method " + method + " exists in class " + cController.getName() + " but not for parameters " + params.toString());
            else
                throw new Exception("Method " + method + " not found in class " + cController.getName());
        }

        return result;
    }

    /**
     * Map input params to method params, if no success, return null
     * 
     * @param iMethod
     * @param inParamsout
     * @return
     */
    private static Object[] mapMethodParams(Method iMethod, List<String> inParams) {
        Class<?> [] mInParamTypes = iMethod.getParameterTypes();
        int noParams = mInParamTypes.length;
        if (noParams == inParams.size()) {
            Object [] mInParams = new Object[inParams.size()];
            
            int index = 0;
            for(Class cType : mInParamTypes) {
                if (cType.isAssignableFrom(String.class)) {
                    mInParams[index] = inParams.get(index);
                }
                else if (cType.isAssignableFrom(Boolean.class)) {
                    mInParams[index] = Boolean.parseBoolean(inParams.get(index));
                }
                else if (cType.isAssignableFrom(Integer.class)) {
                    mInParams[index] = Integer.parseInt(inParams.get(index));
                }
                else if (cType.isAssignableFrom(Long.class)) {
                    mInParams[index] = Long.parseLong(inParams.get(index));
                }
                else if (cType.isAssignableFrom(Double.class)) {
                    mInParams[index] = Double.parseDouble(inParams.get(index));
                }
                else if (cType.isAssignableFrom(Float.class)) {
                    mInParams[index] = Float.parseFloat(inParams.get(index));
                }
                
                index++;
            }
            
            return mInParams;
        } else {
            return null;
        }
    }

    public static JsonNode getRequestJsonFromData(String dataName) throws Exception {
        JsonNode json = request().body().asJson();
        if (json == null) {
            Map<String, String[]> mp = request().body().asFormUrlEncoded();

            String strJson = mp.get(dataName)[0];

            ObjectMapper mapper = new ObjectMapper();
            json = mapper.readTree(strJson);

        }

        return json;
    }

    public static JsonNode getRequestJson() throws Exception {
        return getRequestJsonFromData("json");
    }

    public static HDataContainer getRequestData() throws Exception {
        JsonNode jsn = getRequestJson();
        HDataContainer hdc = new HDataContainer(jsn);
        return hdc;
    }

    public static JsonNode getCallJson() throws Exception {
        return getRequestJsonFromData("call");
    }

    

    
    
}
