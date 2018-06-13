package com.crawljax.plugins.crawloverview.model;

import javax.annotation.concurrent.Immutable;

import com.crawljax.core.CrawljaxException;
import com.crawljax.core.state.Element;
import com.crawljax.core.state.Eventable;
import com.crawljax.forms.FormInput;
import com.crawljax.plugins.crawloverview.CrawlOverviewException;
import com.crawljax.util.DomUtils;
import com.crawljax.util.HarHelper;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import net.lightbody.bmp.core.har.Har;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.NodeDetail;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An {@link Edge} between two {@link State}s.
 */
@Immutable
public class Edge {


	private final String from;
	private final String to;
	private final int hash;
	private final String text;
	private final String id;
	private final String element;
	private final String eventType;
	private final CopyOnWriteArrayList<FormInput> formFields;
	private final String harId;
	private final Har har;

	public Edge(Eventable eventable, Har har) {
		try {
			this.from = eventable.getSourceStateVertex().getName();
			this.to = eventable.getTargetStateVertex().getName();
		} catch (CrawljaxException e) {
			throw new CrawlOverviewException("Could not get state vertex", e);
		}
		this.text = eventable.getElement() != null ? eventable.getElement().getText() : "unknown";
		this.hash = buildHash();
		this.id = eventable.getIdentification() != null ? eventable.getIdentification().toString() : "0";
        this.harId = UUID.randomUUID().toString();
        this.har = har;
		this.formFields = eventable.getRelatedFormInputs();
		Element el = eventable.getElement();
		if (el == null) {
			element = "unkown";
		} else {
			element = eventable.getElement().toString();
		}
		eventType = eventable.getEventType() != null ? eventable.getEventType().toString() : "unknown";
	}

	@JsonCreator
	public Edge(@JsonProperty("from") String from, @JsonProperty("to") String to,
				@JsonProperty("hash") int hash, @JsonProperty("text") String text,
				@JsonProperty("id") String id, @JsonProperty("element") String element,
				@JsonProperty("eventType") String eventType, @JsonProperty("har") Har har) {
		this.from = from;
		this.to = to;
		this.hash = hash;
		this.text = text;
		this.id = id;
		this.formFields = null;
		this.element = element;
		this.eventType = eventType;
		this.harId = UUID.randomUUID().toString();
		this.har = har;
	}

	/**
	 * @return The pre-computed hashcode.
	 */
	private final int buildHash() {
		return Objects.hashCode(from, to, text, id, element, eventType);
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return "Edge [from=" + from + ", to=" + to + ", text=" + text + "]";
	}

	public String getId() {
		return id;
	}

	public String getEventType() {
		return eventType;
	}

	public String getElement() {
		return element;
	}

	@JsonIgnore
	public Har getHar() {
		return har;
	}

	public String getHarId() { return harId; }

	public CopyOnWriteArrayList<FormInput> getFormFields() {
		return formFields;
	}

	public void calculateStateDiff() {

	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Edge) {
			Edge that = (Edge) object;
			return Objects.equal(this.from, that.from)
			        && Objects.equal(this.to, that.to)
			        && Objects.equal(this.text, that.text)
			        && Objects.equal(this.id, that.id)
			        && Objects.equal(this.element, that.element)
			        && Objects.equal(this.eventType, that.eventType);
		}
		return false;
	}

}