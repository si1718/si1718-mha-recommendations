package data.streaming.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Recommendation {
	private String idProject;
	private Set<String> projects;
	
	
	public Recommendation() {
		super();
	}
	public Recommendation(String idProject, Set<String> projects) {
		super();
		this.idProject = idProject;
		this.projects = projects;
		this.projects.remove(idProject);
	}	
	public Recommendation(String idProject, List<String> projects) {
		super();
		this.idProject = idProject;
		this.projects = new HashSet<String>(projects);
		this.projects.remove(idProject);
	}
	
	public String getIdProject() {
		return idProject;
	}
	public void setIdProject(String idProject) {
		this.idProject = idProject;
	}
	public Set<String> getProjects() {
		return projects;
	}
	public void setProjects(Set<String> projects) {
		this.projects = projects;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idProject == null) ? 0 : idProject.hashCode());
		result = prime * result + ((projects == null) ? 0 : projects.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Recommendation other = (Recommendation) obj;
		if (idProject == null) {
			if (other.idProject != null)
				return false;
		} else if (!idProject.equals(other.idProject))
			return false;
		if (projects == null) {
			if (other.projects != null)
				return false;
		} else if (!projects.equals(other.projects))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Recommendation [idProject=" + idProject + ", projects=" + projects + "]";
	}
	
	
}
