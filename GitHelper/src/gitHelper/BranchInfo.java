package gitHelper;

public class BranchInfo 
{
	public String user;
	public String repo;
	public String branchName;
	public String localDir;
	public String repositoryURL;

	public BranchInfo() {
	}

	public BranchInfo
	(
	String user,
	String repo,
	String branchName,
	String localDir,
	String repositoryURL
	) 
	{
		this.user = user;
		this.repo = repo;
		this.branchName = branchName;
		this.localDir = localDir;
		this.repositoryURL = repositoryURL;
	}
}

