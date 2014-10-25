package de.lathspell.test.webservices.exceptions;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Data Transfer Object for the JSON body of a REST error response.
 *
 * Descriptions are quoted from the specs:
 * http://tools.ietf.org/html/draft-nottingham-http-problem-04
 */
@XmlRootElement
public class ApiProblem {

    public static final String APPLICATION_API_PROBLEM_JSON = "application/api-problem+json; charset=UTF-8";
    public static final MediaType APPLICATION_API_PROBLEM_JSON_TYPE = MediaType.valueOf(APPLICATION_API_PROBLEM_JSON);

    public static final String APPLICATION_API_PROBLEM_XML = "application/api-problem+xml; charset=UTF-8";
    public static final MediaType APPLICATION_API_PROBLEM_XML_TYPE = MediaType.valueOf(APPLICATION_API_PROBLEM_XML);

    /**
     * An absolute URI [RFC3986] that identifies the problem type.
     *
     * When dereferenced, it SHOULD provide human-readable documentation
     * for the problem type (e.g., using HTML).
     * REQUIRED.
     */
    @XmlElement
    private String problemType;

    /**
     * A short, human-readable summary of the problem type.
     *
     * It SHOULD NOT change from occurrence to occurrence of the
     * problem, except for purposes of localisation.
     * REQUIRED.
     */
    @XmlElement
    private String title;

    /**
     * The HTTP status code ([RFC2616], Section 6) generated by the origin
     * server for this occurrence of the problem.
     *
     * Generators MUST use the same status code in the actual HTTP response.
     * OPTIONAL.
     */
    @XmlElement
    private Integer httpStatus;

    /**
     * An absolute URI [RFC3986] that identifies the problem type.
     *
     * When dereferenced, it SHOULD provide human-readable documentation
     * for the problem type (e.g., using HTML).
     * OPTIONAL.
     */
    @XmlElement
    private String detail;

    /**
     * An absolute URI that identifies the specific occurrence of the problem.
     *
     * It may or may not yield further information if dereferenced.
     * OPTIONAL.
     */
    @XmlElement
    private String problemInstance;

    /**
     * Extension Members.
     *
     * If such additional members are defined, their names SHOULD start with
     * a letter (ALPHA, as per [RFC5234]) and SHOULD consist of characters
     * from ALPHA, DIGIT and "_" (so that it can be serialised in formats
     * other than JSON), and SHOULD be three characters or longer.
     * OPTIONAL.
     */
    @XmlTransient
    private Map<String, String> extras;

    public ApiProblem() {
        this.extras = new HashMap<>();
    }

    public String getProblemType() {
        return problemType;
    }

    public void setProblemType(String problemType) {
        this.problemType = problemType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getProblemInstance() {
        return problemInstance;
    }

    public void setProblemInstance(String problemInstance) {
        this.problemInstance = problemInstance;
    }

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }

}
