/*
 * Md. Momin Al Aziz momin.aziz.cse @ gmail.com	
 * http://www.mominalaziz.com
 */
package Database;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author shad942
 */
@Entity
@Table(name = "words")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Words.findAll", query = "SELECT w FROM Words w"),
    @NamedQuery(name = "Words.findById", query = "SELECT w FROM Words w WHERE w.id = :id"),
    @NamedQuery(name = "Words.findByWords", query = "SELECT w FROM Words w WHERE w.words = :words"),
    @NamedQuery(name = "Words.findByServerGreaterLesser", query = "SELECT w FROM Words w WHERE w.server > :server and w.server < :server1"),
    @NamedQuery(name = "Words.findByServer", query = "SELECT w FROM Words w WHERE w.server = :server")})
public class Words implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "words")
    private String words;
    @Basic(optional = false)
    @Column(name = "server")
    private int server;

    public Words() {
    }

    public Words(Integer id) {
        this.id = id;
    }

    public Words(Integer id, String words, int server) {
        this.id = id;
        this.words = words;
        this.server = server;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public int getServer() {
        return server;
    }

    public void setServer(int server) {
        this.server = server;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Words)) {
            return false;
        }
        Words other = (Words) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Database.Words[ id=" + id + " ]";
    }
    
}
