package com.topcoder.shared.problem;

import com.topcoder.shared.language.Language;

/**
 * Interface for rendering an element (like a problem component) to HTML or plain text.  The
 * underlying renderer has already been linked to the appropriate element.  Simply call the toHTML
 * method with the appropriate language to render to.
 *
 * @author Tim "Pops" Roberts
 * @version 1.0
 * @version $Id: Renderer.java 71757 2008-07-17 09:13:19Z qliu $
 */
 
// Note: this is part of the plugin API javadoc.  Please be sure to
// keep the javadoc comments up to date.  When implementing changes,
// be sure to regenerate/repackage the plugin API javadoc.

public interface Renderer {

    /**
     * A <code>Renderer</code> capable of transforming the information in
     * its corresponding problem component into a language-specific HTML fragment.
     *
     * @param language  The language for which the HTML fragment should be generated.  E.g., if the
     *                  language is C++, references to array types should be generated as vector
     *                  template instantiations (a transformation the <code>Language</code> object
     *                  is intended to perform).
     * @return A fragment of HTML.
     * @throws Exception if there is a problem instantiating one of the necessary renderers.
     * @see com.topcoder.shared.language.Language
     * @see com.topcoder.shared.language.JavaLanguage
     * @see com.topcoder.shared.language.CPPLanguage
     * @see com.topcoder.shared.language.CSharpLanguage
     */
    public String toHTML(Language language) throws Exception;

    /**
     * A <code>Renderer</code> capable of transforming the information in
     * its corresponding element into a language-specific plain text fragment.
     *
     * Note: the all html tags are stripped from the problem statement.  This may result in an
     * incorrect plain text problem statement if the problem statement depends on those tags.
     *
     * @param language  The language for which the plain test should be generated.  E.g., if the
     *                  language is C++, references to array types would be generated as vector
     *                  template instantiations (a transformation the <code>Language</code> object
     *                  is intended to perform).
     * @return A plain text description of the problem.
     * @throws Exception if there is a problem instantiating one of the necessary renderers.
     * @see com.topcoder.shared.language.Language
     * @see com.topcoder.shared.language.JavaLanguage
     * @see com.topcoder.shared.language.CPPLanguage
     * @see com.topcoder.shared.language.CSharpLanguage
     * @deprecated
     */
    public String toPlainText(Language language) throws Exception;

}

