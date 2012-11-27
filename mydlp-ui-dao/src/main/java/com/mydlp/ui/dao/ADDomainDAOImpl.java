package com.mydlp.ui.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mydlp.ui.domain.ADDomain;
import com.mydlp.ui.domain.ADDomainGroup;
import com.mydlp.ui.domain.ADDomainItem;
import com.mydlp.ui.domain.ADDomainItemGroup;
import com.mydlp.ui.domain.ADDomainOU;
import com.mydlp.ui.domain.ADDomainRoot;
import com.mydlp.ui.domain.ADDomainUser;
import com.mydlp.ui.domain.AbstractEntity;


@Repository("adDomainDAO")
@Transactional
public class ADDomainDAOImpl extends AbstractPolicyDAO implements ADDomainDAO {

	@Override
	@Transactional(readOnly=false)
	public ADDomain saveDomain(ADDomain domain) {
		getHibernateTemplate().saveOrUpdate(domain);
		return domain;
	}

	@Override
	@Transactional(readOnly=false)
	public ADDomainItem saveDomainItem(ADDomainItem domainItem) {
		getHibernateTemplate().saveOrUpdate(domainItem);
		return domainItem;
	}

	@Override
	@Transactional(readOnly=false)
	public void remove(AbstractEntity domainObj) {
		getHibernateTemplate().delete(domainObj);
	}

	@Override
	public ADDomainItem findByDistinguishedName(String distinguishedName) {
		DetachedCriteria criteria = 
				DetachedCriteria.forClass(ADDomainItem.class)
				.add(Restrictions.eq("distinguishedName", distinguishedName));
		if (distinguishedName != null)
			criteria = criteria.add(Restrictions.eq("distinguishedNameHash", new Integer(distinguishedName.hashCode())));
		@SuppressWarnings("unchecked")
		List<ADDomainItem> l = getHibernateTemplate().findByCriteria(criteria);
		return DAOUtil.getSingleResult(l);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ADDomain> getADDomains() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ADDomain.class);
		return getHibernateTemplate().findByCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ADDomainItem> getChildrenOf(ADDomainItemGroup domainItemGroup) {
		DetachedCriteria criteria = 
				DetachedCriteria.forClass(ADDomainItem.class)
				.add(Restrictions.eq("parent.id", domainItemGroup.getId()));
		return getHibernateTemplate().findByCriteria(criteria);
	}

	@Override
	public void finalizeProcess(Integer domainId) {
		getHibernateTemplate().bulkUpdate(
				"update from ADDomain d set d.currentlyEnumerating=false where d.id=?", 
				domainId);
	}

	@Override
	public void startProcess(Integer domainId) {
		getHibernateTemplate().bulkUpdate(
				"update from ADDomain d set d.currentlyEnumerating=true where d.id=?", 
				domainId);
	}

	@Override
	public AbstractEntity merge(AbstractEntity domainObj) {
		return getHibernateTemplate().merge(domainObj);
	}

	@Override
	public ADDomainRoot getDomainRoot(Integer domainId) {
		DetachedCriteria criteria = 
				DetachedCriteria.forClass(ADDomainRoot.class)
				.add(Restrictions.eq("domain.id", domainId));
		@SuppressWarnings("unchecked")
		List<ADDomainRoot> l = getHibernateTemplate().findByCriteria(criteria);
		return DAOUtil.getSingleResult(l);
	}

	@Override
	public ADDomain getDomainById(Integer id) {
		DetachedCriteria criteria = 
				DetachedCriteria.forClass(ADDomain.class)
				.add(Restrictions.eq("id", id));
		@SuppressWarnings("unchecked")
		List<ADDomain> l = getHibernateTemplate().findByCriteria(criteria);
		return DAOUtil.getSingleResult(l);
	}

	@Override
	public List<ADDomainItem> getFilteredADDomains(String searchString) {
		List<ADDomainItem> result = new ArrayList<ADDomainItem>();
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ADDomainGroup.class);
		Disjunction disjunction = Restrictions.disjunction();
		disjunction.add(Restrictions.sqlRestriction("(1=0)")); //  defaults to false
		disjunction.add(Restrictions.ilike("name", "%" + searchString + "%"));
		//disjunction.add(Restrictions.ilike("distinguishedName", "%" + searchString + "%"));
		criteria = criteria.add(disjunction);
		
		@SuppressWarnings("unchecked")
		List<ADDomainItem> result1  = getHibernateTemplate().findByCriteria(criteria);
		
		DetachedCriteria criteria2 = DetachedCriteria.forClass(ADDomainOU.class);
		Disjunction disjunction2 = Restrictions.disjunction();
		disjunction2.add(Restrictions.sqlRestriction("(1=0)")); //  defaults to false
		disjunction2.add(Restrictions.ilike("name", "%" + searchString + "%"));
		//disjunction2.add(Restrictions.ilike("distinguishedName", "%" + searchString + "%"));
		criteria2 = criteria2.add(disjunction2);
		
		@SuppressWarnings("unchecked")
		List<ADDomainItem> result2  = getHibernateTemplate().findByCriteria(criteria2);
		
		//DetachedCriteria subquery = DetachedCriteria.forClass(ADDomainUser.class, "aliases");
		
		DetachedCriteria criteria3 = DetachedCriteria.forClass(ADDomainUser.class);
		Disjunction disjunction3 = Restrictions.disjunction();
		disjunction3.add(Restrictions.sqlRestriction("(1=0)")); //  defaults to false
		disjunction3.add(Restrictions.ilike("displayName", "%" + searchString + "%"));
		disjunction3.add(Restrictions.ilike("sAMAccountName", "%" + searchString + "%"));
		//disjunction3.add(Restrictions.ilike("distinguishedName", "%" + searchString + "%"));
		//disjunction3.add(Restrictions.ilike("aliases.userAlias", searchString));
		criteria3 = criteria3.add(disjunction3);
		
		@SuppressWarnings("unchecked")
		List<ADDomainItem> result3  = getHibernateTemplate().findByCriteria(criteria3);
		
		result.addAll(result1);
		result.addAll(result2);
		result.addAll(result3);
		
		return result;
	}

}
