package com.punit.sts.nova.tools;

import com.punit.sts.nova.AbstractNovaS2SEventHandler;
import com.punit.sts.nova.event.PromptStartEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;

/**
 * S2S Event Handler that is aware of the date and time via tools.
 */
public class DateTimeNovaS2SEventHandler extends AbstractNovaS2SEventHandler {
    private static final Logger log = LoggerFactory.getLogger(DateTimeNovaS2SEventHandler.class);
    private static final String TIMEZONE = System.getenv().getOrDefault("TZ", "America/Los_Angeles");

    public void processTool(String toolName, String content, Map<String, Object> output) {
        if (toolName == null) {
            log.warn("Tool name is null");
            return;
        }

        switch (toolName) {
            case "getDateTool": {
                handleGetDateTool(output);
                break;
            }
            case "getTimeTool": {
                handleGetTimeTool(output);
                break;
            }
            case "getDateAndTimeTool": {
                handleGetDateAndTimeTool(output);
                break;
            }
            case "trackPaymentTool": {
                handleTrackPaymentTool(output);
                break;
            }
            case "getstockvaluetool": {
                handleGetStockValueTool(output);
                break;
            }
            case "knowledgeBase": {
                handleKnowledgeBaseTool(output);
                break;
            }
            default: {
                log.warn("Unhandled tool: {}", toolName);
                output.put("error", "Tool not implemented in backend");
            }
        }
    }

    @Override
    protected void handleToolInvocation(String toolUseId, String toolName, String content, Map<String, Object> output) {
        if (toolName == null) {
            log.warn("Received null toolName");
            //            return;
        } else {
            switch (toolName) {
                case "getDateTool": {
                    handleGetDateTool(output);
                    break;

                }
                case "getTimeTool": {
                    handleGetTimeTool(output);
                    break;
                }
                case "getDateAndTimeTool": {
                    handleGetDateTimeISO(output);
                    break;
                }
                case "trackPaymentTool": {
                    handleTrackPaymentTool(output);
                    break;
                }
                case "getstockvaluetool": {
                    handleGetStockValueTool(content, output);
                    break;
                }
                case "knowledgeBase": {
                    handleKnowledgeBaseTool(output);
                    break;
                }
                default: {
                    log.warn("Unhandled tool: {}", toolName);
                    output.put("error", "Tool not implemented in backend");

                }
            }
        }
    }

    @Override
    public PromptStartEvent.ToolConfiguration getToolConfiguration() {
        return PromptStartEvent.ToolConfiguration.builder()
                .tools(Arrays.asList(
                        PromptStartEvent.Tool.builder()
                                .toolSpec(PromptStartEvent.ToolSpec.builder()
                                        .name("getDateTool")
                                        .description("get information about the current date")
                                        .inputSchema(ToolSpecs.DEFAULT_TOOL_SPEC)
                                        .build()).build(),
                        PromptStartEvent.Tool.builder()
                                .toolSpec(
                                        PromptStartEvent.ToolSpec.builder()
                                                .name("getTimeTool")
                                                .description("get information about the current time")
                                                .inputSchema(ToolSpecs.DEFAULT_TOOL_SPEC)
                                                .build()).build()
                ))
                .build();
    }

    /**
     * Handles a request to get the time.
     * @param contentNode The content node to write the response to.
     */
    private static void handleGetTimeTool(Map<String, Object> contentNode) {
        ZonedDateTime localTime = ZonedDateTime.now(ZoneId.of(TIMEZONE));
        contentNode.put("timezone", TIMEZONE);
        contentNode.put("formattedTime", localTime.format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    /**
     * Handles a request to get the date.
     * @param contentNode The content node to write the response to.
     */
    private static void handleGetDateTool(Map<String, Object> contentNode) {
        LocalDate currentDate = LocalDate.now(ZoneId.of(TIMEZONE));
        contentNode.put("date", currentDate.format(DateTimeFormatter.ISO_DATE));
        contentNode.put("year", currentDate.getYear());
        contentNode.put("month", currentDate.getMonthValue());
        contentNode.put("day", currentDate.getDayOfMonth());
        contentNode.put("dayOfWeek", currentDate.getDayOfWeek().toString());
        contentNode.put("timezone", TIMEZONE);
    }

    private void handleGetDateAndTimeTool(Map<String, Object> output) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(TIMEZONE));
        String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));
        output.put("date", formattedDate.split(" ")[0]);
        output.put("time", formattedDate.split(" ")[1]);
        output.put("timezone", formattedDate.split(" ")[2]);
    }

    private void handleGetDateTimeISO(Map<String, Object> output) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(TIMEZONE));
        String isoDateTime = now.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        output.put("datetime", isoDateTime);
    }

    private void handleTrackPaymentTool(Map<String, Object> output) {
        output.put("status", "Payment ID processed successfully.");
        output.put("estimatedCompletion", LocalDate.now().plusDays(1).toString());
    }

    private void handleGetStockValueTool(String stock, Map<String, Object> output) {
        String resp= performCurl(stock);
        output.put("company_name", stock);
        output.put("stock_value", resp);
    }

    private void handleKnowledgeBaseTool(Map<String, Object> output) {
        output.put("summary", "Paytm saw a 25% increase in wallet transactions in Q4.");


    }

    private String performCurl(String stock) {

       String urlprefix= "https://awspe.com/api/price?stock=" +stock.trim();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlprefix))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            log.error("Curl failed for URL: {}", urlprefix, e);
            return "{}";
        }
    }

}
